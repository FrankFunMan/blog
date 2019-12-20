<template>
  <div class="index">
    <el-row :gutter="20">
      <el-col :span="4">
        <div v-for="item in imgs" class="imgs">
          <el-card :body-style="{ padding: '0px' }">
            <img :src="item" class="image">
          </el-card>
        </div>
      </el-col>
      <el-col :span="12">
        <el-card :body-style="{ padding: '0px' }">
          <el-carousel height="308px">
            <el-carousel-item v-for="item in imgs" :key="item">
              <img :src="item">
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card >
          <el-row :gutter="20">
            <el-col :span="6">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false"><i class="el-icon-platform-eleme"
                  style="font-size: 50px;"></i></el-link>
            </el-col>
            <el-col :span="6">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false"><i class="el-icon-platform-eleme"
                  style="font-size: 50px;"></i></el-link>
            </el-col>
            <el-col :span="6">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false"><i class="el-icon-platform-eleme"
                  style="font-size: 50px;"></i></el-link>
            </el-col>
            <el-col :span="6">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false"><i class="el-icon-platform-eleme"
                  style="font-size: 50px;"></i></el-link>
            </el-col>
          </el-row>
        </el-card>
        <el-card :body-style="{ padding: '0px' }">
          <div slot="header">
            <span class="card-title">热门专题</span>
          </div>
          <el-carousel height="150px" direction="vertical">
            <el-carousel-item v-for="item in imgs" :key="item">
              <img :src="item">
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </el-col>
    </el-row>


    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span class="card-title">热门排行</span>
          </div>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="1">
              <div class="rank">1</div>
            </el-col>
            <el-col :span="20">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                MySql从删库到跑路
              </el-link>
            </el-col>
            <el-col :span="3">
              <div class="like">
                <i class="el-icon-apple"></i>
                1314喜欢
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div slot="header">
            <span class="card-title">友情链接</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
            <el-col :span="5">
              <el-link href="https://element.eleme.io" target="_blank" :underline="false">
                友情链接
              </el-link>
            </el-col>
          </el-row>
        </el-card>

      </el-col>
    </el-row>
  </div>
</template>
<script>
  export default {
    name: 'Index',
    data() {
      return {
        imgs: [
          'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png',
          'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png',
          'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png',
          'https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png'
        ]
      };
    }
  }
</script>
<style scoped>
  .image {
    height: 80px;
  }

  .el-card {
    margin-bottom: 7px;
  }

  .imgs .el-card {
    margin-bottom: 2px;
    cursor: pointer;
  }

  .imgs .el-card:hover {
    border: solid #409EFF 2px;
  }

  .card-title {
    font-weight: bolder;
    color: #409EFF;
    letter-spacing: 5px;
  }

  .like {
    color: palevioletred;
  }

  .like i {
    font-size: 20px;
  }

  .rank {
    text-align: center;
    line-height: 30px;
    color: #fff;
    width: 30px;
    height: 30px;
    background-color: #409EFF;
  }

  .el-row {
    margin-bottom: 10px;
  }
</style>
