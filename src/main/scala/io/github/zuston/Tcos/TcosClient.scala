package io.github.zuston.Tcos

import com.qcloud.cos.{COSClient, ClientConfig}
import com.qcloud.cos.request.UploadFileRequest
import com.qcloud.cos.sign.Credentials
import io.github.zuston.Helper.PropertyHelper
import io.github.zuston.Util.ConstUtil

/**
  * Created by zuston on 17/6/24.
  */
object TcosClient {

  val helper = new PropertyHelper
  helper.load(ConstUtil.TCOS_PATH)

  val appId = helper.getLong("appId",0)
  val secretId = helper.get("secretId")
  val secretKey = helper.get("secretKey")

  val cred = new Credentials(appId,secretId,secretKey)

  val clientConfig = new ClientConfig()

  val cosClient = new COSClient(clientConfig, cred)

  private def revert(retString : String) : TcosReturnType = {
    // TODO:
    new TcosReturnType
  }


  def upload(targetPathAndFileName: String, bucketName : String, originFilePath: String): TcosReturnType = {
    val uploadFileRequest = new UploadFileRequest(bucketName,targetPathAndFileName,originFilePath)
    val ret = cosClient.uploadFile(uploadFileRequest)
    revert(ret)

  }

  def download() : Unit = {

  }

}




