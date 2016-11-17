package com.sunnybear.rxandroid.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public class Baike implements Serializable {
    /**
     * id : 5181
     * subLemmaId : 8577636
     * newLemmaId : 40416
     * key : 外滩
     * desc : 上海外滩
     * title : 外滩
     * card : [{"key":"m42_cname","name":"中文名称","value":["外滩"],"format":["外滩"]},{"key":"m42_ename","name":"外文名称","value":["The Bund"],"format":["The Bund"]},{"key":"m42_location","name":"地理位置","value":["上海市<a target=_blank href=\"/view/90491.htm\">黄浦区<\/a>、<a target=_blank href=\"/view/193779.htm\">虹口区<\/a>"],"format":["上海市<a target=_blank href=\"/view/90491.htm\">黄浦区<\/a>、<a target=_blank href=\"/view/193779.htm\">虹口区<\/a>"]},{"key":"m42_climate","name":"气候类型","value":["<a target=_blank href=\"/view/47993.htm\">亚热带季风气候<\/a>"],"format":["<a target=_blank href=\"/view/47993.htm\">亚热带季风气候<\/a>"]},{"key":"m42_openhours","name":"开放时间","value":["全天"],"format":["全天"]},{"key":"m42_ticketprices","name":"门票价格","value":["免费开放"],"format":["免费开放"]},{"key":"m42_famousview","name":"著名景点","value":["<a target=_blank href=\"/view/102116.htm\">外白渡桥<\/a>、<a target=_blank href=\"/view/447130.htm\">黄浦公园<\/a>、<a target=_blank href=\"/view/913539.htm\">沙逊大厦<\/a>、<a target=_blank href=\"/view/213836.htm\">百老汇大厦<\/a>等"],"format":["<a target=_blank href=\"/view/102116.htm\">外白渡桥<\/a>、<a target=_blank href=\"/view/447130.htm\">黄浦公园<\/a>、<a target=_blank href=\"/view/913539.htm\">沙逊大厦<\/a>、<a target=_blank href=\"/view/213836.htm\">百老汇大厦<\/a>等"]},{"key":"m42_ext_0","name":"建筑年代","value":["1906年－1937年"],"format":["1906年－1937年"]},{"key":"m42_ext_1","name":"保护单位","value":["<a target=_blank href=\"/view/163959.htm\">全国重点文物保护单位<\/a>"],"format":["<a target=_blank href=\"/view/163959.htm\">全国重点文物保护单位<\/a>"]}]
     * image : http://imgsrc.baidu.com/baike/pic/item/b3fb43166d224f4ac7e60cc601f790529922d1ec.jpg
     * src : b3fb43166d224f4ac7e60cc601f790529922d1ec
     * imageHeight : 408
     * imageWidth : 720
     * isSummaryPic : y
     * abstract : 外滩（英文：The Bund；上海话拼音：nga thae），位于上海市中心黄浦区的黄浦江畔，即外黄浦滩，1844年（清道光廿四年）起这一带被划为英国租界，成为上海十里洋场的真实写照，也是旧上海租界区以及整个上海近代城市开始的起点。外滩全长1.5公里，南起延安东路，北至苏州河上的外白渡桥，东面即黄浦江，西面是旧上海金融、外贸机构的集中地。1943年起，外滩的正式路名为中山东一路。上海辟为商埠以后，外国的银行、商行、总会、报社开始在此云集。外滩迅速成为全国乃至远东的金融中心，1943年8月，外滩随交还上海公共租界于汪精卫政权，外滩也随著上海结束长达百年的租界时期。自上海开埠后，外滩就开始成为上海乃至中国的金融及贸易中心，也是旧上海资本主义的写照，一直以来被视为上海的标志性建筑和城市历史的象征。与外滩隔江相对的浦东陆家嘴，有上海标志性建筑东方明珠、金茂大厦、上海中心、上海环球金融中心等，则成为...
     * moduleIds : [124272446,124272447,124272448,124272449,124272450,124272451,124272452,124272453]
     * url : http://baike.baidu.com/subview/5181/8577636.htm
     * wapUrl : http://wapbaike.baidu.com/subview/5181/8577636.htm
     * hasOther : 1
     * totalUrl : http://baike.baidu.com/view/5181.htm
     * catalog : ["<a href='http://baike.baidu.com/subview/5181/8577636.htm#1'>实用信息<\/a>","<a href='http://baike.baidu.com/subview/5181/8577636.htm#2'>交通信息<\/a>","<a href='http://baike.baidu.com/subview/5181/8577636.htm#3'>主要景点<\/a>","<a href='http://baike.baidu.com/subview/5181/8577636.htm#4'>地下隧道<\/a>"]
     * wapCatalog : ["<a href='http://wapbaike.baidu.com/subview/5181/8577636.htm#1'>实用信息<\/a>","<a href='http://wapbaike.baidu.com/subview/5181/8577636.htm#2'>交通信息<\/a>","<a href='http://wapbaike.baidu.com/subview/5181/8577636.htm#3'>主要景点<\/a>","<a href='http://wapbaike.baidu.com/subview/5181/8577636.htm#4'>地下隧道<\/a>"]
     * logo : http://img.baidu.com/img/baike/logo-baike.gif
     * copyrights : 以上内容来自百度百科平台，由百度百科网友创作。
     * customImg :
     */

    private int id;
    private int subLemmaId;
    private int newLemmaId;
    private String key;
    private String desc;
    private String title;
    private String image;
    private String src;
    private String imageHeight;
    private String imageWidth;
    private String isSummaryPic;
    private String abstractX;
    private String url;
    private String wapUrl;
    private int hasOther;
    private String totalUrl;
    private String logo;
    private String copyrights;
    private String customImg;
    /**
     * key : m42_cname
     * name : 中文名称
     * value : ["外滩"]
     * format : ["外滩"]
     */

    private List<Card> card;
    private List<Integer> moduleIds;
    private List<String> catalog;
    private List<String> wapCatalog;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubLemmaId() {
        return subLemmaId;
    }

    public void setSubLemmaId(int subLemmaId) {
        this.subLemmaId = subLemmaId;
    }

    public int getNewLemmaId() {
        return newLemmaId;
    }

    public void setNewLemmaId(int newLemmaId) {
        this.newLemmaId = newLemmaId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getIsSummaryPic() {
        return isSummaryPic;
    }

    public void setIsSummaryPic(String isSummaryPic) {
        this.isSummaryPic = isSummaryPic;
    }

    public String getAbstractX() {
        return abstractX;
    }

    public void setAbstractX(String abstractX) {
        this.abstractX = abstractX;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWapUrl() {
        return wapUrl;
    }

    public void setWapUrl(String wapUrl) {
        this.wapUrl = wapUrl;
    }

    public int getHasOther() {
        return hasOther;
    }

    public void setHasOther(int hasOther) {
        this.hasOther = hasOther;
    }

    public String getTotalUrl() {
        return totalUrl;
    }

    public void setTotalUrl(String totalUrl) {
        this.totalUrl = totalUrl;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public String getCustomImg() {
        return customImg;
    }

    public void setCustomImg(String customImg) {
        this.customImg = customImg;
    }

    public List<Card> getCard() {
        return card;
    }

    public void setCard(List<Card> card) {
        this.card = card;
    }

    public List<Integer> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<Integer> moduleIds) {
        this.moduleIds = moduleIds;
    }

    public List<String> getCatalog() {
        return catalog;
    }

    public void setCatalog(List<String> catalog) {
        this.catalog = catalog;
    }

    public List<String> getWapCatalog() {
        return wapCatalog;
    }

    public void setWapCatalog(List<String> wapCatalog) {
        this.wapCatalog = wapCatalog;
    }

    @Override
    public String toString() {
        return "Baike{" +
                "id=" + id +
                ", subLemmaId=" + subLemmaId +
                ", newLemmaId=" + newLemmaId +
                ", key='" + key + '\'' +
                ", desc='" + desc + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", src='" + src + '\'' +
                ", imageHeight='" + imageHeight + '\'' +
                ", imageWidth='" + imageWidth + '\'' +
                ", isSummaryPic='" + isSummaryPic + '\'' +
                ", abstractX='" + abstractX + '\'' +
                ", url='" + url + '\'' +
                ", wapUrl='" + wapUrl + '\'' +
                ", hasOther=" + hasOther +
                ", totalUrl='" + totalUrl + '\'' +
                ", logo='" + logo + '\'' +
                ", copyrights='" + copyrights + '\'' +
                ", customImg='" + customImg + '\'' +
                ", card=" + card +
                ", moduleIds=" + moduleIds +
                ", catalog=" + catalog +
                ", wapCatalog=" + wapCatalog +
                '}';
    }
}
