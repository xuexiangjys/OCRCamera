# OCRCamera

一个能够快速识别卡片的智能照相机，可配合百度OCR文字识别使用。本案例中只使用了百度OCR的行驶证识别能力。

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)   [![简书](https://img.shields.io/badge/简书-xuexiangjys-red.svg)](https://www.jianshu.com/u/6bf605575337)   [![掘金](https://img.shields.io/badge/掘金-xuexiangjys-brightgreen.svg)](https://juejin.im/user/598feef55188257d592e56ed)   [![知乎](https://img.shields.io/badge/知乎-xuexiangjys-violet.svg)](https://www.zhihu.com/people/xuexiangjys) 

## 演示

![orc_camera.gif](https://img.rruu.net/image/5fc661dc1cb6e)

## 集成说明

* [百度OCR 行驶证识别](https://ai.baidu.com/tech/ocr_cars/vehicle_license)

* [SmartCamera](https://github.com/pqpo/SmartCamera)

* [XUI 一个简洁而优雅的Android原生UI框架，解放你的双手！](https://github.com/xuexiangjys/XUI)

* [XPage 一个非常方便的fragment页面框架。](https://github.com/xuexiangjys/XPage)

## 如何运行

1.请在[百度AI开发平台](https://ai.baidu.com/)上注册开发者账号，并且注册应用，获取对应的`API Key`和`Secret Key`。

2.请在项目的`local.properties`中填写你的`API Key`和`Secret Key`. key 分别为 `OCR_APP_KEY` 和 `OCR_SECRET_KEY`。

![ocr_key.jpeg](https://img.rruu.net/image/5fc654fce7e0a)

## 如何定制修改

修改OCRCameraFragment的`doOCRAction`方法，在里面完成自己的识别逻辑。


## 如果觉得项目还不错，可以考虑打赏一波

> 你的打赏是我维护的动力，我将会列出所有打赏人员的清单在下方作为凭证，打赏前请留下打赏项目的备注！

![pay.png](https://img.rruu.net/image/5f871d00045da)

## 联系方式

> 更多资讯内容，欢迎扫描关注我的个人微信公众号:【我的Android开源之旅】

![gzh_weixin.jpg](https://img.rruu.net/image/5f871cfff3194)