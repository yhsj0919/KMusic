# KMusic

## 该代码仅作技术交流,切勿用作商业用途

### 音乐网站api
- 音乐解析部分来自 [musicdl](https://github.com/CharlesPikachu/musicdl) ,部分为个人整理
- 其它未实现的接口请查看 [API](https://github.com/yhsj0919/KMusic/blob/master/API.md) ,时间久远，不保证全部能用
- 不支持付费歌曲,没做付费歌曲过滤(人品好的话,付费歌曲也可以播放)
- 可一次查询6个网站的信息,也可以单独查询
- 有问题或者建议,欢迎issues,不定期修改
- 支持android,理论上支持java(部分kotlin代码没有做专门优化)
- 虾米音乐不再支持，2021年(qua)要(xian)关(gao)服(bu)了(ding)
- 新的bd音乐，整理了手机客户端90%以上的接口

# 支持网站

| 站点 |  搜索 | 排行榜 | 专辑 | mv |
| :--: |  :-----: | :-----: |  :-----: |  :-----: |
| bd     |✓|✓| | |
| qq      |✓|✓|| |
| kg     |✓|✓| | |
| wy   |✓|✓| | |
| kw     |✓|✓| | |
| mg     |✓|✓| | |


# 简单使用
```kotlin
//返回6个网站的所有数据
val resp = KMusic.searchAll(key = songName!!)

//指定网站查询
val resp = KMusic.search(key = songName!!,site = MusicSite.QQ)

```
