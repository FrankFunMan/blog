package com.zj.blog.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zj.blog.exception.ApiMallPlusException;
import com.zj.blog.marking.entity.*;
import com.zj.blog.marking.vo.SmsCouponHistoryDetail;
import com.zj.blog.oms.entity.OmsCartItem;
import com.zj.blog.oms.entity.OmsOrder;
import com.zj.blog.oms.entity.OmsOrderItem;
import com.zj.blog.oms.entity.OmsOrderSetting;
import com.zj.blog.oms.mapper.OmsOrderMapper;
import com.zj.blog.oms.mapper.OmsOrderSettingMapper;
import com.zj.blog.oms.vo.*;
import com.zj.blog.pms.entity.PmsProduct;
import com.zj.blog.pms.entity.PmsSkuStock;
import com.zj.blog.pms.mapper.PmsSkuStockMapper;
import com.zj.blog.pms.service.IPmsProductService;
import com.zj.blog.ums.entity.UmsIntegrationConsumeSetting;
import com.zj.blog.ums.entity.UmsMember;
import com.zj.blog.ums.entity.UmsMemberReceiveAddress;
import com.zj.blog.ums.mapper.UmsIntegrationConsumeSettingMapper;
import com.zj.blog.ums.service.IUmsMemberReceiveAddressService;
import com.zj.blog.ums.service.IUmsMemberService;
import com.zj.blog.ums.service.RedisService;
import com.zj.blog.util.DateUtils;
import com.zj.blog.util.UserUtils;
import com.zj.blog.util.applet.TemplateData;
import com.zj.blog.util.applet.WX_TemplateMsgUtil;
import com.zj.blog.utils.CommonResult;
import com.zj.blog.config.WxAppletProperties;
import com.zscat.mallplus.marking.entity.*;
import com.zj.blog.marking.service.ISmsCouponHistoryService;
import com.zj.blog.marking.service.ISmsCouponService;
import com.zj.blog.marking.service.ISmsGroupMemberService;
import com.zj.blog.marking.service.ISmsGroupService;
import com.zj.blog.oms.service.IOmsCartItemService;
import com.zj.blog.oms.service.IOmsOrderItemService;
import com.zj.blog.oms.service.IOmsOrderService;
import com.zscat.mallplus.oms.vo.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zscat
 * @since 2019-04-17
 */
@Service
@Slf4j
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderMapper, OmsOrder> implements IOmsOrderService {

    @Resource
    private RedisService redisService;
    @Value("${redis.key.prefix.orderId}")
    private String REDIS_KEY_PREFIX_ORDER_ID;
    @Resource
    private IPmsProductService productService;
    @Resource
    private IUmsMemberReceiveAddressService addressService;

    @Autowired
    private WxAppletProperties wxAppletProperties;

    @Resource
    private WechatApiService wechatApiService;
    @Resource
    private ISmsGroupService groupService;
    @Resource
    private ISmsGroupMemberService groupMemberService;
    @Resource
    private IOmsCartItemService cartItemService;

    @Resource
    private ISmsCouponService couponService;
    @Resource
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Resource
    private PmsSkuStockMapper skuStockMapper;
    @Resource
    private ISmsCouponHistoryService couponHistoryService;
    @Resource
    private IOmsOrderService orderService;
    @Resource
    private IOmsOrderItemService orderItemService;
    @Resource
    private OmsOrderMapper orderMapper;
    @Resource
    private IUmsMemberService memberService;
    @Resource
    private OmsOrderSettingMapper orderSettingMapper;

    @Override
    public int payOrder(TbThanks tbThanks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time=sdf.format(new Date());
        tbThanks.setTime(time);
        tbThanks.setDate(new Date());
        /*TbMember tbMember=tbMemberMapper.selectByPrimaryKey(Long.valueOf(tbThanks.getUserId()));
        if(tbMember!=null){
            tbThanks.setUsername(tbMember.getUsername());
        }
        if(tbThanksMapper.insert(tbThanks)!=1){
            throw new XmallException("保存捐赠支付数据失败");
        }*/

        //设置订单为已付款
        OmsOrder tbOrder=orderMapper.selectById(tbThanks.getOrderId());
        if (tbOrder==null){
            throw new ApiMallPlusException("订单不存在");
        }
        tbOrder.setStatus(1);
        tbOrder.setPayType(tbThanks.getPayType());
        tbOrder.setPaymentTime(new Date());
        tbOrder.setModifyTime(new Date());
        if(orderMapper.updateById(tbOrder)!=1){
            throw new ApiMallPlusException("更新订单失败");
        }
        //恢复所有下单商品的锁定库存，扣减真实库存
        OmsOrderItem queryO = new OmsOrderItem();
        queryO.setOrderId(tbThanks.getOrderId());
        List<OmsOrderItem> list = orderItemService.list(new QueryWrapper<>(queryO));

        int count = orderMapper.updateSkuStock(list);
        //发送通知确认邮件
        String tokenName= UUID.randomUUID().toString();
        String token= UUID.randomUUID().toString();

        // emailUtil.sendEmailDealThank(EMAIL_SENDER,"【mallcloud商城】支付待审核处理",tokenName,token,tbThanks);
        return count;
    }
    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectById(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //发送延迟消息
        //  cancelOrderSender.sendMessage(orderId, delayTimes);
    }

    /**
     *
     * @return
     */
    @Override
    public ConfirmOrderResult submitPreview(OrderParam orderParam) {

        String type =orderParam.getType();

        UmsMember currentMember = UserUtils.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList =new ArrayList<>();
        if ("3".equals(type)){ // 1 商品详情 2 勾选购物车 3全部购物车的商品
            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),null);
        }
        if( "1".equals(type)) {
            String cartId = orderParam.getCartId();
            if (org.apache.commons.lang.StringUtils.isBlank(cartId)){
                throw new ApiMallPlusException("参数为空");
            }
            OmsCartItem omsCartItem = cartItemService.selectById(Long.valueOf(cartId));
            List<OmsCartItem> list = new ArrayList<>();
            list.add(omsCartItem);
            if (!CollectionUtils.isEmpty(list)) {
                cartPromotionItemList = cartItemService.calcCartPromotion(list);
            }
        }else if ("2".equals(type)){
            String cart_id_list1 =orderParam.getCartIds();
            if (org.apache.commons.lang.StringUtils.isBlank(cart_id_list1)){
                throw new ApiMallPlusException("参数为空");
            }
            String[] ids1 =cart_id_list1.split(",");
            List<Long> resultList = new ArrayList<>(ids1.length);
            for (String s : ids1) {
                resultList.add(Long.valueOf(s));
            }
            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),resultList);
        }
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息

        result.setCartPromotionItemList(cartPromotionItemList);
        //获取用户收货地址列表
        UmsMemberReceiveAddress queryU = new UmsMemberReceiveAddress();
        queryU.setMemberId(currentMember.getId());
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>(queryU));
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        UmsMemberReceiveAddress address = addressService.getDefaultItem();
        //获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(cartPromotionItemList, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);
        result.setAddress(address);
        return result;

    }
    @Override
    public ConfirmOrderResult generateConfirmOrder() {
        ConfirmOrderResult result = new ConfirmOrderResult();
        //获取购物车信息
        UmsMember currentMember = UserUtils.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),null);
        result.setCartPromotionItemList(cartPromotionItemList);
        //获取用户收货地址列表
        UmsMemberReceiveAddress queryU = new UmsMemberReceiveAddress();
        queryU.setMemberId(currentMember.getId());
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>(queryU));
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        //获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(cartPromotionItemList, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);
        return result;
    }

    @Override
    public CommonResult generateOrder(OrderParam orderParam) {

        String type =orderParam.getType();
        UmsMember currentMember = UserUtils.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList =new ArrayList<>();
        if ("3".equals(type)){ // 1 商品详情 2 勾选购物车 3全部购物车的商品
            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),null);
        }
        if( "1".equals(type)) {
            Long cartId = Long.valueOf(orderParam.getCartId());
            OmsCartItem omsCartItem = cartItemService.selectById(cartId);
            List<OmsCartItem> list = new ArrayList<>();
            list.add(omsCartItem);
            if (!CollectionUtils.isEmpty(list)) {
                cartPromotionItemList = cartItemService.calcCartPromotion(list);
            }
        }else if ("2".equals(type)){
            String cart_id_list1 =orderParam.getCartIds();
            String[] ids1 =cart_id_list1.split(",");
            List<Long> resultList = new ArrayList<>(ids1.length);
            for (String s : ids1) {
                resultList.add(Long.valueOf(s));
            }
            cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),resultList);
        }


        List<OmsOrderItem> orderItemList = new ArrayList<>();
        //获取购物车及优惠信息
        String name="";

        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            //生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItemList.add(orderItem);
            name = cartPromotionItem.getProductName();
        }
        //判断购物车中商品是否都有库存
        if (!hasStock(cartPromotionItemList)) {
            return new CommonResult().failed("库存不足，无法下单");
        }
        //判断使用使用了优惠券
        if (orderParam.getCouponId() == null) {
            //不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            //使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if (couponHistoryDetail == null) {
                return new CommonResult().failed("该优惠券不可用");
            }
            //对下单商品的优惠券进行处理
            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        //判断是否使用积分
        if (orderParam.getUseIntegration() == null) {
            //不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            //使用积分
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
                return new CommonResult().failed("积分不可用");
            } else {
                //可用情况下分摊到可用商品中
                for (OmsOrderItem orderItem : orderItemList) {
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        //计算order_item的实付金额
        handleRealAmount(orderItemList);
        //进行库存锁定
        lockStock(cartPromotionItemList);
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        } else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        } else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        //转化为订单信息并插入数据库
        order.setMemberId(currentMember.getId());
        order.setCreateTime(new Date());
        order.setMemberUsername(currentMember.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        //收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = addressService.getById(orderParam.getAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        //计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        orderService.save(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemService.saveBatch(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        //如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            memberService.updateIntegration(currentMember.getId(), currentMember.getIntegration() - orderParam.getUseIntegration());
        }
        //删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);

        String platform = orderParam.getPlatform();
        if("1".equals(platform)){
            push(currentMember,order,orderParam.getPage(),orderParam.getFormId(),name);
        }
        return new CommonResult().success("下单成功", result);
    }

    @Override
    public CommonResult paySuccess(Long orderId) {
        //修改订单支付状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(1);
        order.setPaymentTime(new Date());
        orderService.updateById(order);
        //恢复所有下单商品的锁定库存，扣减真实库存
        OmsOrderItem queryO = new OmsOrderItem();
        queryO.setOrderId(orderId);
        List<OmsOrderItem> list = orderItemService.list(new QueryWrapper<>(queryO));
        int count = orderMapper.updateSkuStock(list);
        return new CommonResult().success("支付成功", count);
    }

    /**
     * 推送消息
     *
     */
    public void push(UmsMember umsMember, OmsOrder order,String page,String formId,String name) {
        log.info("发送模版消息：userId="+umsMember.getId()+",orderId="+order.getId()+",formId="+formId);
        if (StringUtils.isEmpty(formId)) {
            log.error("发送模版消息：userId="+umsMember.getId()+",orderId="+order.getId()+",formId="+formId);
        }
        String accessToken = null;
        try {
            accessToken = wechatApiService.getAccessToken();

            String templateId = wxAppletProperties.getTemplateId();
            Map<String, TemplateData> param = new HashMap<String, TemplateData>();
            param.put("keyword1", new TemplateData(DateUtils.format(order.getCreateTime(),"yyyy-MM-dd"), "#EE0000"));

            param.put("keyword2", new TemplateData(name, "#EE0000"));
            param.put("keyword3", new TemplateData(order.getOrderSn(), "#EE0000"));
            param.put("keyword3", new TemplateData(order.getPayAmount()+"", "#EE0000"));

            JSONObject jsonObject = JSONObject.fromObject(param);
            //调用发送微信消息给用户的接口    ********这里写自己在微信公众平台拿到的模板ID
            WX_TemplateMsgUtil.sendWechatMsgToUser(umsMember.getWeixinOpenid(), templateId, page+"?id="+order.getId(),
                    formId, jsonObject, accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public CommonResult cancelTimeOutOrder() {
        OmsOrderSetting orderSetting = orderSettingMapper.selectById(1L);
        //查询超时、未支付的订单及订单详情
        List<OmsOrderDetail> timeOutOrders = orderMapper.getTimeOutOrders(orderSetting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) {
            return new CommonResult().failed("暂无超时订单");
        }
        //修改订单状态为交易取消
        List<Long> ids = new ArrayList<>();
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            ids.add(timeOutOrder.getId());
        }
        orderMapper.updateOrderStatus(ids, 4);
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            //解除订单商品库存锁定
            orderMapper.releaseSkuStockLock(timeOutOrder.getOrderItemList());
            //修改优惠券使用状态
            updateCouponStatus(timeOutOrder.getCouponId(), timeOutOrder.getMemberId(), 0);
            //返还使用积分
            if (timeOutOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(timeOutOrder.getMemberId());
                memberService.updateIntegration(timeOutOrder.getMemberId(), member.getIntegration() + timeOutOrder.getUseIntegration());
            }
        }
        return new CommonResult().success(null);
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询为付款的取消订单

        OmsOrder cancelOrder = orderMapper.selectById(orderId);
        if (cancelOrder != null) {
            //修改订单状态为取消
            cancelOrder.setStatus(4);
            orderMapper.updateById(cancelOrder);
            OmsOrderItem queryO = new OmsOrderItem();
            queryO.setOrderId(orderId);
            List<OmsOrderItem> list = orderItemService.list(new QueryWrapper<>(queryO));
            //解除订单商品库存锁定
            orderMapper.releaseSkuStockLock(list);
            //修改优惠券使用状态
            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
            //返还使用积分
            if (cancelOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(cancelOrder.getMemberId());
                memberService.updateIntegration(cancelOrder.getMemberId(), member.getIntegration() + cancelOrder.getUseIntegration());
            }
        }
    }
    @Override
    public Object preSingelOrder(GroupAndOrderVo orderParam) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        result.setGroupAndOrderVo(orderParam);
        PmsProduct goods = productService.getById(orderParam.getGoodsId());
        result.setGoods(goods);
        //获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = addressService.list(new QueryWrapper<>());
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        UmsMemberReceiveAddress address = addressService.getDefaultItem();

        result.setAddress(address);
        return result;
    }
    /**
     * 推送消息
     */
    public void push(GroupAndOrderVo umsMember, OmsOrder order, String page, String formId) {
        log.info("发送模版消息：userId=" + umsMember.getMemberId() + ",orderId=" + order.getId() + ",formId=" + formId);
        if (StringUtils.isEmpty(formId)) {
            log.error("发送模版消息：userId=" + umsMember.getMemberId() + ",orderId=" + order.getId() + ",formId=" + formId);
        }
        String accessToken = null;
        try {
            accessToken = wechatApiService.getAccessToken();

            String templateId = wxAppletProperties.getTemplateId();
            Map<String, TemplateData> param = new HashMap<String, TemplateData>();
            param.put("keyword1", new TemplateData(DateUtils.format(order.getCreateTime(), "yyyy-MM-dd"), "#EE0000"));

            param.put("keyword2", new TemplateData(order.getGoodsName(), "#EE0000"));
            param.put("keyword3", new TemplateData(order.getOrderSn(), "#EE0000"));
            param.put("keyword3", new TemplateData(order.getPayAmount() + "", "#EE0000"));

            JSONObject jsonObject = JSONObject.fromObject(param);
            //调用发送微信消息给用户的接口    ********这里写自己在微信公众平台拿到的模板ID
            WX_TemplateMsgUtil.sendWechatMsgToUser(umsMember.getWxid(), templateId, page + "?id=" + order.getId(),
                    formId, jsonObject, accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Transactional
    @Override
    public Object generateSingleOrder(GroupAndOrderVo orderParam,UmsMember member) {
        String type = orderParam.getType();
        orderParam.setMemberId(member.getId());
        orderParam.setName(member.getIcon());
        PmsProduct goods = productService.getById(orderParam.getGoodsId());

        if (goods.getStock() < 0) {
            return new CommonResult().failed("库存不足，无法下单");
        }


        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(goods.getPrice());
        order.setPayAmount(goods.getPrice());
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(new BigDecimal(0));

        order.setSupplyId(goods.getSupplyId());
        order.setCouponAmount(new BigDecimal(0));

        order.setIntegration(0);
        order.setIntegrationAmount(new BigDecimal(0));


        order.setGoodsId(goods.getId());
        order.setGoodsName(order.getGoodsName());
        //转化为订单信息并插入数据库
        order.setMemberId(orderParam.getMemberId());
        order.setCreateTime(new Date());
        order.setMemberUsername(member.getUsername());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(orderParam.getSourceType());
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(orderParam.getOrderType());
        //收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = addressService.getById(orderParam.getAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(0);
        //计算赠送成长值
        order.setGrowth(0);
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        SmsGroup group = groupService.getById(orderParam.getGroupId());
        if (group!=null){
            order.setPayAmount(group.getGroupPrice());
        }
        // TODO: 2018/9/3 bill_*,delivery_*
        //插入order表和order_item表
        this.save(order);


        if ("0".equals(type)) { // 0 下单 1 拼团 2 发起拼团

        }
        if ("1".equals(type)) {
            SmsGroupMember sm = new SmsGroupMember();
            sm.setGroupId(orderParam.getGroupId());
            sm.setMemberId(orderParam.getMemberId());
            List<SmsGroupMember> smsGroupMemberList = groupMemberService.list(new QueryWrapper<>(sm));
            if (smsGroupMemberList!=null && smsGroupMemberList.size()>0){
                return new CommonResult().failed("你已经参加此拼团");
            }

            Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(group.getEndTime(), group.getHours()), "yyyy-MM-dd HH:mm:ss");
            Long nowT = System.currentTimeMillis();
            if (nowT > group.getStartTime().getTime() && nowT < endTime.getTime()) {
                if (orderParam.getMemberId() == null || orderParam.getMemberId() < 1) {
                    orderParam.setMemberId(orderParam.getMainId());
                }
                orderParam.setStatus(2);
                orderParam.setCreateTime(new Date());
                orderParam.setOrderId(order.getId());
                groupMemberService.save(orderParam);
            } else {
                return new CommonResult().failed("活动已经结束");
            }
        } else if ("2".equals(type)) {
            group = groupService.getById(orderParam.getGroupId());
            Date endTime = DateUtils.convertStringToDate(DateUtils.addHours(group.getEndTime(), group.getHours()), "yyyy-MM-dd HH:mm:ss");
            Long nowT = System.currentTimeMillis();
            if (nowT > group.getStartTime().getTime() && nowT < endTime.getTime()) {
                if (orderParam.getMemberId() == null || orderParam.getMemberId() < 1) {
                    orderParam.setMemberId(orderParam.getMainId());
                }
                orderParam.setStatus(2);
                orderParam.setCreateTime(new Date());
                orderParam.setOrderId(order.getId());
                orderParam.setMainId(orderParam.getMemberId());
                groupMemberService.save(orderParam);
            } else {
                return new CommonResult().failed("活动已经结束");
            }

        }
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);

        if (orderParam.getSourceType() == 1) {
            push(orderParam, order, orderParam.getPage(), orderParam.getFormId());
        }
        return new CommonResult().success("下单成功", result);
    }


    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        sb.append(order.getMemberId());
        return sb.toString();
    }
    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 锁定下单商品的所有库存
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            PmsSkuStock skuStock = skuStockMapper.selectById(cartPromotionItem.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
            skuStockMapper.updateById(skuStock);
        }
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (cartPromotionItem.getRealStock() <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }


    /**
     * 删除下单商品的购物车信息
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartItemService.delete(currentMember.getId(), ids);
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null) {return;}
        //查询第一张优惠券
        SmsCouponHistory queryC= new SmsCouponHistory();
        queryC.setCouponId(couponId);
        if (useStatus == 0){
            queryC.setUseStatus(1);
        }else {
            queryC.setUseStatus(0);
        }
        List<SmsCouponHistory> couponHistoryList = couponHistoryService.list(new QueryWrapper<>(queryC));
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryService.updateById(couponHistory);
        }
    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销价格-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(",");
        }
        String result = sb.toString();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取可用积分抵扣金额
     *
     * @param useIntegration 使用的积分数量
     * @param totalAmount    订单总金额
     * @param currentMember  使用的用户
     * @param hasCoupon      是否已经使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember, boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        //判断用户是否有这么多积分
        if (useIntegration.compareTo(currentMember.getIntegration()) > 0) {
            return zeroAmount;
        }
        //根据积分使用规则判断使用可用
        //是否可用于优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectById(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            //不可与优惠券共用
            return zeroAmount;
        }
        //是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 对优惠券优惠进行处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 可用优惠券详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            //全场通用
            calcPerCouponAmount(orderItemList, coupon);
        } else if (coupon.getUseType().equals(1)) {
            //指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 0);
            calcPerCouponAmount(couponOrderItemList, coupon);
        } else if (coupon.getUseType().equals(2)) {
            //指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList, 1);
            calcPerCouponAmount(couponOrderItemList, coupon);
        }
    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);
        for (OmsOrderItem orderItem : orderItemList) {
            //(商品价格/可用商品总价)*优惠券面额
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList, int type) {
        List<OmsOrderItem> result = new ArrayList<>();
        if (type == 0) {
            List<Long> categoryIdList = new ArrayList<>();
            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail.getCategoryRelationList()) {
                categoryIdList.add(productCategoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        } else if (type == 1) {
            List<Long> productIdList = new ArrayList<>();
            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                productIdList.add(productRelation.getProductId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIdList.contains(orderItem.getProductId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        }
        return result;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
        List<SmsCouponHistoryDetail> couponHistoryDetailList = couponService.listCart(cartPromotionItemList, 1);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                return couponHistoryDetail;
            }
        }
        return null;
    }
}
