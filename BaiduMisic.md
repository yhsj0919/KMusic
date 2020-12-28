# **百度音乐**

### sign算法

<details>
<summary>说明</summary>

```kotlin
val params = "params1=测试&params2=测试2"
private fun sign(params: String): String {
    val secret = "0b50b02fd0d73a9c4c8c3a781c30845f"
    val data = "$params$secret"
    return MD5.md5(data)
}
```

</details>

### baseUrl

`https://api-qianqian.taihe.com`

### 开屏

[开屏](https://api-qianqian.taihe.com/v1/ad/openscreen?timestamp=1608603519533&sign=febf6df8fea2bd6addc9e16411c111d5)

### 首页

[首页](https://api-qianqian.taihe.com/v1/index?timestamp=1608603521586&sign=becf73087ed10283ff94b7bde16e3aff)

### 专辑信息

banner详情也用

[专辑信息](https://api-qianqian.taihe.com/v1/album/info?albumAssetCode=P10003604399&timestamp=1608606628078&sign=a757abb7ed6d515d297ea1b55e6cd84b)

[最新专辑](https://api-qianqian.taihe.com/v1/album/list?pageNo=1&pageSize=20&timestamp=1608613949638&sign=0b6610139ef5f8e6fc1131ff436829b7)

### 歌曲详情(播放地址。Vip歌曲只能获取30秒)

[新歌推荐](https://api-qianqian.taihe.com/v1/song/list?pageNo=1&pageSize=20&timestamp=1608614160873&sign=09a0a2d0c4007ae4bc30151a46766e83)

[歌曲详情](https://api-qianqian.taihe.com/v1/song/tracklink?TSID=T10058580675&timestamp=1608606920969&sign=dde6cc223c9a02f798cd232becea2053)

### 歌手列表

<details>
  <summary>说明</summary>

请求头加入，不然无法获取全部歌手

from: android

搜索参数

artistRegion=内地、港台、欧美、韩国、日本、其他

artistGender=男、女、组合、乐队

pageNo=1

pageSize=30

</details>

[歌手列表1](https://api-qianqian.taihe.com/v1/artist/list?timestamp=1608607509776&sign=69616a77c866f0987704696179c2c08a)

[歌手列表2](https://api-qianqian.taihe.com/v1/artist/list?artistRegion=内地&pageNo=1&pageSize=30&timestamp=1608607779186&sign=0d80138dfaa706342d908bf46951395a)

[歌手列表3](https://api-qianqian.taihe.com/v1/artist/list?artistGender=男&artistRegion=内地&pageNo=1&pageSize=30&timestamp=1608607839041&sign=9b78240a447dae931f4018a0eab68dbe)

[歌手详情](https://api-qianqian.taihe.com/v1/artist/info?artistCode=A10047720&timestamp=1608609366066&sign=b10c72f412848f0d5800a774524054ef)

[热门单曲](https://api-qianqian.taihe.com/v1/artist/song?artistCode=A10047720&pageNo=1&pageSize=20&timestamp=1608609557892&sign=0e72180cb0aa7cebfbc70d2e84306b7a)

[歌手专辑](https://api-qianqian.taihe.com/v1/artist/album?artistCode=A10047720&pageNo=1&pageSize=20&timestamp=1608609557916&sign=b8fbfa18f5fde470c9317d402384d313)

### 搜索

<details>
  <summary>说明</summary>
type:
0全部、1单曲、3专辑、2歌手

0没有分页，其他有 pageNo、pageSize
</details>


[搜索](https://api-qianqian.taihe.com/v1/search?timestamp=1608608525310&type=0&word=%E8%96%9B%E4%B9%8B%E8%B0%A6&sign=b932c0e706d38c28bb8251869aa52949)
[搜索](https://api-qianqian.taihe.com/v1/search?pageNo=1&pageSize=20&timestamp=1608859692317&type=1&word=%E8%96%9B%E4%B9%8B%E8%B0%A6&sign=09cff6888257567afba7257c54d7f8cb)

[热词提示](https://api-qianqian.taihe.com/v1/search/sug?timestamp=1608616072902&word=wohewode&sign=79fa197ba1437570fd708bfb3a351042)

### 榜单

[榜单分类](https://api-qianqian.taihe.com/v1/bd/category?timestamp=1608613635960&sign=2b7715385a3df8c8152a8ff2af989b1c)

[榜单详情](https://api-qianqian.taihe.com/v1/bd/list?bdid=257851&pageNo=1&pageSize=20&timestamp=1608613636150&sign=59546b23c1dcbfeb70ab0e1d962661af)

### 歌曲分类(流派,歌单)

[歌曲分类](https://api-qianqian.taihe.com/v1/tracklist/category?timestamp=1608613815703&sign=88f1f4559cb9e65553707b8ff72044c0)

[分类详情](https://api-qianqian.taihe.com/v1/tracklist/list?pageNo=1&pageSize=20&subCateId=2368&timestamp=1608613879054&sign=dd3e0907c6ec357a999685642a74ec36)

[推荐歌单](https://api-qianqian.taihe.com/v1/tracklist/list?pageNo=1&pageSize=20&timestamp=1608614026245&sign=e246ddb81e2b7e6c7f4e7e330dd47a2e)

[歌单信息](https://api-qianqian.taihe.com/v1/tracklist/info?id=271333&pageNo=1&pageSize=50&timestamp=1608606764153&sign=728e88732853e8cff260fc9893fa9ed3)

### 手机号登陆

<details>
  <summary>说明</summary>
post请求

phone=18612345678

randstr=@arV(貌似随机值，不知道啥用)

暂时不知道计算方法，(貌似没啥用，去掉也能发送短信)
ticket=t03KZbFM9PLpwVKp9VtrKDy28GNMvby8kxub07GvXqAVOtieIjlWRgO5oIJsJg6wDTg331ckmu1hjhAM1XHrRH5IOcsPZAXqzEAu6XkImoSaTBgHCqf7a5vsQ**

timestamp=1608614495995

sign=b0e11dddd
</details>


[发送验证码](https://api-qianqian.taihe.com/v1/oauth/send_sms)

<details>
  <summary>说明</summary>
post请求(表单)

code=4806

phone=18612345678

timestamp=1608614552585

sign=4504ce8758f402ddd
</details>


[登录](https://api-qianqian.taihe.com/v1/oauth/login)

[退出账号](https://api-qianqian.taihe.com/v1/account/logout)
timestamp=1608617893884&sign=dc5d907a2aa189ffed561b40e6d697d0

### 账户信息

<details>
<summary>说明</summary>
请求头放入authorization,这个是在登录之后返回的

`authorization:access_token ZGU1JTljMjBkOTZmODQyNzQ4N2NzMzQyMzM5YjYxMzM=`
</details>

<details>
<summary>修改说明</summary>
修改客户信息用post
age=25&avatar=https://avatar-qianqian.taihe.com/c055c43e759dcfc887.jpeg&birth=1995-01-01&nickname=测试&sex=1&timestamp=1608965799010&sign=c51b66996ad6fb12d9
</details>

[账户信息](https://api-qianqian.taihe.com/v1/account/info?timestamp=1608614552995&sign=9bdbaa168656cf59087b814767d0aa3e)

<details>
<summary>头像说明</summary>
Content-Disposition: form-data; name="targetPath"
Content-Length: 37
06453255cb700104f052a9abbacd5c78.jpeg

Content-Disposition: form-data; name="secureKey"
Content-Length: 32
140598208fb5921d01a00b9cbf210e4f(这个貌似是个MD5,暂时不知道怎么计算，和上面的targetPath有关)

Content-Disposition: form-data; name="file"; filename="06453255cb700104f052a9abbacd5c78.jpeg"
Content-Type: image/jpeg Content-Length: 11040
你要上传的头像

</details>

[上传头像](https://upload-avatar-qianqian.taihe.com/UPLOAD/upload)

[账户歌曲列表，不知道干啥用的，貌似没展示的地方](https://api-qianqian.taihe.com/v1/account/songlist?pageNo=1&pageSize=20&timestamp=1608617256944&sign=c9316dff33e92092120a5e068267ef36)

[账户收藏等信息](https://api-qianqian.taihe.com/v1/account/amount?timestamp=1608617256951&sign=9bdd3cae6d72e46cac33b5fc97312ae6)

<details>
<summary>签到</summary>
post请求
timestamp=1608617260391&sign=7da5d83841c79e351d402630ae6b4049
</details>

[签到](https://api-qianqian.taihe.com/v1/user/points/signin)

### 收藏歌曲

<details>
<summary>说明</summary>
参数

code=T10058580675&timestamp=1608614553008&sign=8ee2bb7c11115cee8b2f81d8402e50cd
</details>

[收藏](https://api-qianqian.taihe.com/v1/favorite/song/create)

[收藏歌单](https://api-qianqian.taihe.com/v1/favorite/tracklist/create)

[收藏歌手](https://api-qianqian.taihe.com/v1/favorite/artist/create)

参数同上

[取消收藏歌曲](https://api-qianqian.taihe.com/v1/favorite/song/delete)

[取消收藏歌单](https://api-qianqian.taihe.com/v1/favorite/tracklist/delete)

[取消收藏歌手](https://api-qianqian.taihe.com/v1/favorite/artist/delete)

[收藏的歌曲](https://api-qianqian.taihe.com/v1/favorite/song?pageNo=1&pageSize=20&timestamp=1608617478775&sign=840a0174fd7b58a29480972cd75bf192)

[收藏的歌单](https://api-qianqian.taihe.com/v1/favorite/tracklist?pageNo=1&pageSize=50&timestamp=1608617576648&sign=2ad241d8819df3b9178f07c6aa5a8bfa)

[收藏的歌手](https://api-qianqian.taihe.com/v1/favorite/artist?pageNo=1&pageSize=20&timestamp=1608617620372&sign=872a52902a211a8946cb65ed43d8388c)


###歌曲下载(非得让我冲会员(T_T),感觉亏了500W,vip歌曲需要登陆才能下载)
[歌曲下载](https://api-qianqian.taihe.com/v1/song/download?TSID=T10058207752&timestamp=1608975041150&sign=d160f0e4175b377a82ceba90258aabf0)