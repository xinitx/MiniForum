<script>
import axios from "axios";

const BackEndIP = 'http://localhost:10010/'
const PostingUploadAppendix = BackEndIP + 'posting/add/appendix'
const UserUploadAvatar = BackEndIP + 'user/modify/avatar/'
const TokenAxios = axios.create({})
const wsUri = "ws://localhost:10010/"
const base = ''
TokenAxios.interceptors.response.use(
    (response) => {
        if (response.status === 401) {
            window.location.replace(base + '/login')
            console.log(response)
            return response
        } else {
            console.log(response)
            return response
        }
    }, (error) => {
        console.log(error)
        if (error.response.status === 401) {
            window.location.replace(base + '/login')
        }
        return Promise.reject(error)
    })
TokenAxios.interceptors.request.use(
    config => {
        if(!verifyLogin()){
            window.location.replace(base + '/login')
        }
        let token = localStorage.getItem("weibo_token")
        config.headers['token'] = token;
        return config;
    },
    error => {
        return Promise.reject(error);
    }
)


function verifyLogin() {
    const token = localStorage.getItem('weibo_token');
    const expiryTime = localStorage.getItem('weibo_tokenExpired');
    if (!token || !expiryTime) {
        // 如果未找到 token 或过期时间，则表示 token 已过期
        return false;
    }
    // 将过期时间字符串转换为时间戳
    const now = new Date().getTime();
    const expiry = new Date(expiryTime).getTime();
    return now < expiry;
}


export default {
    // eslint-disable-next-line vue/multi-word-component-names
    name: "common",
    BackEndIP,
    TokenAxios,
    PostingUploadAppendix,
    verifyLogin,
    UserUploadAvatar,
    wsUri,
    base
}
</script>

<style scoped>

</style>