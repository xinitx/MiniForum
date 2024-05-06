<template>
        <div>
            <div class="toolbar">
                <div style="margin-right: 60px">
                    <div @click="userView(user.id)">
                        <el-image :src="user.avatar" style="height: 40px;width: 40px; border-radius: 20px">
                        </el-image>
                    </div>
                    <div style="text-align: center" @click="userView(user.id)">{{user.nickname}}</div>
                </div>
            </div>
            <div style="position: fixed; width: 25%;height: 100%">
                <div class="summaryLeftCSS" style="top: 100px">
                        <el-input
                            placeholder="请输入内容"
                            v-model="searchContent"
                            style="width: 70%;"
                            clearable>
                        </el-input>
                        <el-button type="success" @click="searchPosting">搜索</el-button>
                </div>
                <div class="summaryLeftCSS" style="top: 180px">
                    <el-table :data="postingHot" style="height: 100%" border>
                        <el-table-column prop="posting.title" label="热榜"></el-table-column>
                        <el-table-column prop="posting.record" label="浏览量"/>
                    </el-table>
                </div>
                <div class="summaryLeftCSS" style="bottom: 20px">
                    <el-table border :data="postingLike" style="height: 100%">
                        <el-table-column prop="posting.title" label="关注"></el-table-column>
                        <el-table-column prop="auth.nickname" label="作者"/>
                    </el-table>
                </div>
            </div>
            <div  style="position:absolute;right: 0; width: 75%;height: 85%">
                <div @scroll="infiniteScroll" infinite-scroll-distance="10"  v-infinite-scroll="loadPosting" :infinite-scroll-immediate="infinite_scroll.immediate" :infinite-scroll-disabled="infinite_scroll.disabled" class="infinite-list" >
                        <el-row v-for="posting in postingAll"
                                :key="posting.posting.id"
                                class="postingCSS">
                            <el-col :span="2">
                                <el-row>
                                    <el-image style="margin:auto auto;width: 40px; height: 40px; float: left;" :src="posting.auth.avatar" @click="userView(posting.auth.id)"/>
                                </el-row>
                                <el-row>
                                    <el-text class="mx-1" style="margin:auto auto;font-size: 12px; color: white;overflow: auto;" @click="userView(posting.auth.id)">{{posting.auth.nickname}}</el-text>
                                </el-row>
                            </el-col>
                            <el-col :span="20">
                                <el-row>
                                    <div v-if="posting.tags != null" style="height:24px;">
                                    <el-tag type="info" v-for="tag in posting.tags" :key="tag.id" style="margin-left: 5px">{{tag.content}}</el-tag>
                                    </div>
                                </el-row>
                                <el-row>
                                    <el-text  type="success"  style="font-size: 24px;overflow: auto;" @click="detailView(posting.posting.id)">{{posting.posting.title}}</el-text>
                                </el-row>
                                <el-row>
                                    <el-text  style="font-size: 18px; color: white;overflow: auto;" @click="detailView(posting.posting.id)">&nbsp;&nbsp;{{posting.posting.content}}</el-text>
                                </el-row>
                                <el-row style="margin-top: 20px; margin-left: 20px;display:inline" v-for="(item, index) in posting.files" :key="index">
                                  <span>
                                  <el-image v-if="item.type === 'image/jpeg'" :src="item.url" style="max-width: 100px; max-height: 100px;">
                                  </el-image>
                                  <video v-if="item.type === 'video/mp4'" style="width: 100%;height: 100%; max-width: 400px; max-height: 400px;"  alt="Preview Video" controls>
                                      <source :src="item.url"  type="video/mp4">
                                  </video>
                                  </span>
                                </el-row>
                                <el-row style="height: auto">
                                    <el-icon color="white"><View /></el-icon>
                                    <el-text class="mx-1"   style="font-size: 12px;color: white" @click="detailView(posting.posting.id)">{{posting.posting.record}}</el-text>
                                    <el-icon color="white" style="margin-left: 5px"><Opportunity /></el-icon>
                                    <el-text class="mx-1"   style="font-size: 12px;color: white;" @click="detailView(posting.posting.id)">{{posting.likeSum}}</el-text>
                                    <el-icon color="white" style="margin-left: 5px"><ChatDotRound /></el-icon>
                                    <el-text class="mx-1"   style="font-size: 12px;color: white" @click="detailView(posting.posting.id)">{{posting.forwardSum}}</el-text>
                                    <el-icon color="white" style="margin-left: 5px"><Share /></el-icon>
                                    <el-text class="mx-1"   style="font-size: 12px;color: white" @click="detailView(posting.posting.id)">{{posting.forwardSum}}</el-text>
                                </el-row>
                            </el-col>
                        </el-row>

                </div>
            </div>
        </div>
        <div style="right: 20px; top:120px; position: fixed;" >
            <el-button type="success" @click="postingNew.dialogVisible=true"><el-icon><Edit /></el-icon></el-button>
            <el-affix :offset="160"  v-show="topBack" @click="topBackScroll">
                <el-button type="primary"><el-icon><Top /></el-icon></el-button>
            </el-affix>
        </div>
        <el-dialog
            v-model="postingNew.dialogVisible"
            title="写帖子"
            width="80%"
            align-center
        >
            <el-input
                v-model="postingNew.title"
                :autosize="{ minRows: 1, maxRows: 4 }"
                type="textarea"
                placeholder="标题"
                ref="uploadRef"
            />
            <el-input
                v-model="postingNew.content"
                :autosize="{ minRows: 4, maxRows: 8 }"
                type="textarea"
                placeholder="内容"
                ref="uploadRef"
            />
            <el-tag
                    style="margin: 2px 2px;"
                :key="tag.id"
                v-for="tag in postingNew.tags"
                closable
                :disable-transitions="false"
                @close="postingNewTagClose(tag)">
                {{tag.content}}
            </el-tag>
            <el-input
                class="input-new-tag"
                v-if="postingNew.tagInput.visible"
                v-model="postingNew.tagInput.content"
                style="width: auto;"
                ref="saveTagInput"
                size="small"
                @keyup.enter="postingNewTagConfirm"
                @blur="postingNewTagConfirm"
            >
            </el-input>
            <el-button v-else class="button-new-tag" size="small" @click="postingNew.tagInput.visible=true">+ New Tag</el-button>

            <el-upload action="#"
                       ref="upload"
                       list-type="picture-card"
                       :file-list="postingNew.files"
                       :multiple="true"
                       :auto-upload="false"
                       name="multipartFiles"
                       :on-change="(file,fileList)=>postingNewUploadChange(file,fileList)"
                       :on-success="postingNewUploadChangeSuccess"
            >
                <el-icon><Plus /></el-icon>
                <template #file="{file}">
                    <div>
                        <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" v-if="file.raw.type==='image/jpeg'|| file.raw.type==='image/png'" />
                        <video class="el-upload-list__item-thumbnail" :src="file.url" alt="" v-if="file.raw.type==='video/mp4'" />
                        <span class="el-upload-list__item-actions">
                  <span
                      class="el-upload-list__item-preview"
                      @click="handleFilePreview(file)"
                  >
                    <el-icon><zoom-in /></el-icon>
                  </span>
                  <span
                      v-if="!postingNew.disabled"
                      class="el-upload-list__item-delete"
                      @click="handlePostingNewFileRemove(file)"
                  >
                    <el-icon><Delete /></el-icon>
                  </span>
                </span>
                    </div>
                </template>
            </el-upload>
            <el-dialog v-model="filePreview.visible" >
                <img :src="filePreview.url" style="width: 100%;height: 100%"  alt="Preview Image" v-if="filePreview.type==='image/jpeg' || filePreview.type==='image/png' "/>
                <video style="width: 100%;height: 100%" v-if="filePreview.type==='video/mp4'" alt="Preview Video" controls>
                    <source :src="filePreview.url"  type="video/mp4">
                </video>
            </el-dialog>
            <template #footer>
      <span class="dialog-footer">
        <el-button @click="postingNew.dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="postingNewUpload()">确认</el-button>
      </span>
            </template>
        </el-dialog>
</template>

<script>
import common from "@/components/common.vue";
import axios from "axios";


export default {
    name: "SummaryView",
    data(){
        return{
            infinite_scroll:{
                disabled:false,
                immediate:false,
            },
            searchContent:"",
            postingLoadCount: 1,
            topBack:false,
            user:{
                avatar:"http://127.0.0.1:9000/weibo-avatar/default.jpg",
                nickname: "游客",
                id:-1
            },
            postingAll:[
                {
                    posting:{
                        id:0,
                        title:"test",
                        content:"test",
                        record:0,
                        auth:0,
                        create_time: "2024-02-14T09:56:41",
                    },
                    auth:{id:0, avatar: "http://127.0.0.1:9000/weibo-avatar/default.jpg",nickname:"游客"},
                    tags:[{id:0,content:"test"}, {id:0,content:"test"}],
                    commentSum:0,
                    likeSum:0,
                    forwardSum:0,
                }, {
                    posting:{
                        id:0,
                        title:"test",
                        content:"test",
                        record:0,
                        auth:0,
                        create_time: "2024-02-14T09:56:41",
                    },
                    auth:{id:0, avatar: "http://127.0.0.1:9000/weibo-avatar/default.jpg",nickname:"游客"},
                    tags:[{id:0,content:"test"}, {id:0,content:"test"}],
                    commentSum:0,
                    likeSum:0,
                    forwardSum:0,
                }
            ],
            postingHot:[
                {
                    posting:{
                        id:0,
                        title:"test",
                        content:"test",
                        record:0,
                        auth:0,
                        create_time: "2024-02-14T09:56:41",
                    },
                    auth:{id:0, avatar: "http://127.0.0.1:9000/weibo-avatar/default.jpg",nickname:"游客"},
                    tags:[{id:0,content:"test"}, {id:0,content:"test"}],
                    commentSum:0,
                    likeSum:0,
                    forwardSum:0,
                }
                ],
            postingLike:[
                {
                    posting:{
                        id:0,
                        title:"test",
                        content:"test",
                        record:0,
                        auth:0,
                        create_time: "2024-02-14T09:56:41",
                    },
                    auth:{id:0, avatar: "http://127.0.0.1:9000/weibo-avatar/default.jpg",nickname:"游客"},
                    tags:[{id:0,content:"test"}, {id:0,content:"test"}],
                    commentSum:0,
                    likeSum:0,
                    forwardSum:0,
                }
            ],
            postingNew:{
                  dialogVisible : false,
                  tagInput:{
                      visible: false,
                      content: "",
                  },
                  title:"",
                  content:"",
                  tags:[],
                  files:[],

            },
            filePreview:{
                    visible : false,
                    url:"",
                    type:"",
            }
        }
    },
    mounted() {
        if(common.verifyLogin()){
            this.user.avatar = localStorage.getItem("weibo_avatar")
            this.user.nickname = localStorage.getItem("weibo_nickname")
            this.user.id = localStorage.getItem("weibo_userId")
        }
        axios({
            method:"get",
            url: common.BackEndIP + "posting/all",
            params:{
                userId: this.user.id,
                page: this.postingLoadCount,
            }
        }).then((resp)=>{
            console.log(resp)
            if(resp.data.code === "200"){
              this.postingAll = resp.data.data;
                this.postingHot = this.postingAll.sort((a,b) => {
                    return a.recordSum - b.recordSum
                })
            }else{
                alert(resp.data.msg)
            }
        })
        if(common.verifyLogin()) {
            common.TokenAxios({
                method: "get",
                url: common.BackEndIP + "posting/like",
                params:{
                    userId:this.user.id,
                }
            }).then((resp) => {
                console.log("00000000000000")
                console.log(resp)
                if (resp.data.code === "200") {
                    this.postingLike = resp.data.data
                } else {
                    alert(resp.data.msg)
                }
            })
        }

    },
    methods:{
        userView(targetUserId){
            if(common.verifyLogin()){
                localStorage.setItem("weibo_targetUserId", targetUserId)
                this.$router.push(common.base + '/info')
            }
            else {
                this.$router.push(common.base + '/login')
            }
        },
        detailView(targetPostingId){
            if(common.verifyLogin()){
                localStorage.setItem("weibo_targetPostingId", targetPostingId)
                this.$router.push(common.base + '/detail')
            }
            else {
                this.$router.push(common.base + '/login')
            }
        },
        searchPosting(){
            axios({
                method: "get",
                url: common.BackEndIP + "posting/all",
                params:{
                    userId: this.user.id,
                    page: 1,
                    searchCondition: this.searchContent,
                }
            }).then((resp) => {
                if (resp.data.code === "200") {
                    console.log(resp)
                    this.postingAll = resp.data.data
                    this.infinite_scroll.disabled = false;
                    this.postingLoadCount = 1
                } else {
                    alert(resp.data.msg)
                }
            })
        },
        loadPosting(){
            axios({
                method: "get",
                url: common.BackEndIP + "posting/all",
                params:{
                    userId: this.user.id,
                    page: this.postingLoadCount+1,
                    searchCondition: this.searchContent,
                }
            }).then((resp) => {
                if (resp.data.code === "200") {
                    console.log(resp)
                    this.postingAll.push.apply(this.postingAll,resp.data.data)
                    this.postingLoadCount++
                    console.log(this.postingAll)
                } else {
                    if(this.infinite_scroll.disabled === false)
                        alert(resp.data.msg)
                    this.infinite_scroll.disabled = true;
                }
            })
        },
        postingNewTagConfirm(){
            let inputValue = this.postingNew.tagInput.content;
            if (inputValue) {
                this.postingNew.tags.push({id:-1,content:inputValue});
            }
            this.postingNew.tagInput.visible = false;
            this.postingNew.tagInput.content = '';
        },postingNewTagClose(tag){
            this.postingNew.tags.splice(this.postingNew.tags.indexOf(tag), 1);
        },handleFilePreview(file){
            this.filePreview.url = file.url
            this.filePreview.type = file.raw.type
            this.filePreview.visible = true
        },postingNewUpload(){
            common.TokenAxios({
                method:"post",
                url:common.BackEndIP + 'posting/add',
                data:{
                    posting:{
                        title:this.postingNew.title,
                        content:this.postingNew.content,
                        auth: this.user.id
                    },
                    tags: this.postingNew.tags,
                }
            }).then((resp)=>{
                if(resp.data.code === "200"){
                    if(this.postingNew.files.length !== 0){
                        let formData = new FormData(); //  用FormData存放上传文件
                        // eslint-disable-next-line no-unused-vars
                        this.postingNew.files.forEach((val,index) => {
                            formData.append("multipartFiles",val.raw)
                            formData.append("type",val.raw.type)
                        });
                        formData.set("postingId",resp.data.data)
                        common.TokenAxios({
                            method:"post",
                            url:common.PostingUploadAppendix,
                            data:formData,
                            headers:{
                                "Content-Type": "multipart/form-data"
                            }
                        }).then(resp=>{
                            console.log(resp)
                        })
                    }
                }else {
                    alert(resp.data.msg)
                }
            })
            this.postingNew.dialogVisible = false
        },handlePostingNewFileRemove(file){
            this.$refs.upload.handleRemove(file);
        },postingNewUploadChange(file, fileList){
            console.log(file)
            console.log(fileList)
            this.postingNew.files=fileList
        },postingNewUploadChangeSuccess(){
            location.reload()
        },topBackScroll(){
            this.topBack = false;
            document.querySelector(".infinite-list").scrollTop = 0;
        },infiniteScroll(){
            const top = document.querySelector(".infinite-list").scrollTop
            if(top > 600) this.topBack = true
            else this.topBack = false
        }
    }
}
</script>

<style scoped>
.infinite-list {
    overflow: auto;
    right: 0;
    height: 100%;
    margin: 0;
    margin:100px  0 0;
    list-style: none;
}
.toolbar {
    position: fixed;
    display: inline-flex;
    align-items: center;
    justify-content: right;
    text-align: right;
    font-size: 16px;
    width: 100%;
    height: 15%;
    z-index:10;
    background-color: #a9c09a;
}
.summaryLeftCSS{
    position: absolute;
    width: 80%;
    height: 30%;
    margin: 20px 20px 20px 20px;
}
.postingCSS{
    overflow:auto;
    margin:20px 80px 20px 20px;
    height: auto;
    width: auto;
    background-color: #2c3e50;
    box-shadow: rgba(6, 24, 44, 0.4) 0px 0px 0px 2px, rgba(6, 24, 44, 0.65) 0px 4px 6px -1px, rgba(255, 255, 255, 0.08) 0px 1px 0px inset;
}
span{
    box-shadow: rgba(0, 0, 0, 0.25) 0px 54px 55px, rgba(0, 0, 0, 0.12) 0px -12px 30px, rgba(0, 0, 0, 0.12) 0px 4px 6px, rgba(0, 0, 0, 0.17) 0px 12px 13px, rgba(0, 0, 0, 0.09) 0px -3px 5px;
}
</style>