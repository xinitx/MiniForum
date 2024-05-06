<template>
    <div style="flex: 1; height: 100%; width: 100%">
    <el-row style="height: 100%; width: 100%; padding: 0; margin: 0;">
        <el-col :span="16" style="height: 100%; width: 100%; padding: 0; margin: 0;" class="weibo_back">

        </el-col>
        <el-col :span="8" style="height: 100%; width: 100%; padding: 0; margin: 0;">
            <el-row class="weibo_form" style="height: 100%; width: 100%; padding: 0; margin: 0;">
                <div style="margin:auto auto;">
                <el-col>
                    <el-row style="font-size: 36px;"><div style="margin: 0 auto;"><b>登 录</b></div></el-row>
                <el-row>
                <el-form :model="user" :rules="rules" ref="userForm">
                    <el-form-item prop="username">
                        <el-input size="default"  v-model="user.username" placeholder="请输入账号"></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input size="default" v-model="user.password" show-password placeholder="请输入密码"></el-input>
                    </el-form-item>
                    <div v-if="IsRegister">
                    <el-form-item prop="confirmPassword">
                        <el-input v-model="user.confirmPassword" show-password placeholder="请确认密码"></el-input>
                    </el-form-item>
                    <el-form-item prop="nickname">
                        <el-input size="default" v-model="user.nickname" placeholder="请输入昵称"></el-input>
                    </el-form-item>
                    </div>
                    <el-row>
                        <div style="margin: 0 auto;">
                        <el-button type="primary"  autocomplete="off" @click="LoginAndRegister()">确定</el-button>
                        <el-button type="warning" autocomplete="off" @click="freshUser()" v-if="!IsRegister">注册</eL-button>
                            <el-button type="warning" autocomplete="off" @click="freshUser()" v-if="IsRegister">返回</eL-button>
                        </div>
                    </el-row>

                </el-form>
                </el-row>
                </el-col>
            </div>
            </el-row>

        </el-col>
    </el-row>
    </div>
</template>

<script>
/*import common from "@/components/common.vue";*/
import axios from "axios";
import common from "@/components/common.vue";
export default {
    name: "LoginAndRegisterView",
    data(){
        const validPassword = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'));
            } else if (value !== this.user.password) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        };
        return{
            user: {},
            IsRegister: false,
            rules: {
                username:[
                    {required:true, message:'请输入账号', trigger: 'blur'},
                    {min:3, max:10,message: '长度在3到10个字符', trigger: 'blur'}
                ],password:[
                    {required:true, message:'请输入密码', trigger: 'blur'},
                    {min:6, max:18,message: '长度在6到18个字符', trigger: 'blur'}
                ],confirmPassword:[
                    {required:true, message:'请确认密码', trigger: 'blur'},
                    {min:6, max:18,message: '长度在6到18个字符', trigger: 'blur'},
                    {validator:validPassword, trigger: "blur"},
                ],nickname:[
                    {required:true, message:'请输入昵称', trigger: 'blur'},
                    {min:3, max:10,message: '长度在3到10个字符', trigger: 'blur'}
                ]
            },
        }
    },
    mounted() {
        if(common.verifyLogin()){
            this.$router.push(common.base + "/summary")
        }
    },
    methods:{
        LoginAndRegister(){
            this.$refs['userForm'].validate((valid)=>{
                if(valid){
                    let targetURL = "user/login"
                    if(this.IsRegister){
                        targetURL = "user/register"
                    }
                    axios({
                        method:"post",
                        url: common.BackEndIP + targetURL,
                        data: this.user,
                    }).then((resp)=>{
                        console.log(resp)
                        if(resp.data.code === "200"){
                            localStorage.setItem("weibo_token", resp.data.data.token)
                            localStorage.setItem("weibo_tokenExpired", resp.data.data.tokenExpired)
                            localStorage.setItem("weibo_userId", resp.data.data.user.id)
                            localStorage.setItem("weibo_nickname", resp.data.data.user.nickname)
                            localStorage.setItem("weibo_avatar", resp.data.data.user.avatar)
                            localStorage.setItem("weibo_introduce", resp.data.data.user.introduce)
                            this.$router.push(common.base + "/summary")
                        }else {
                            this.notify('警告', resp.data.msg, 'warning')
                        }
                    })
                }
            })
        },
        notify(title, msg,type) {
            this.$notify({
                title: title,
                message: msg,
                position: 'top-left',
                type: type,
            });
        },
        freshUser(){
            this.IsRegister = !this.IsRegister
            this.user = {}
        }
    }
}
</script>

<style scoped>
.weibo_form{
    background: linear-gradient(to bottom right,#66ccff,#3F5EFB);
}
.weibo_back{
    background-image:url('src/assets/background.jpg');
}
</style>