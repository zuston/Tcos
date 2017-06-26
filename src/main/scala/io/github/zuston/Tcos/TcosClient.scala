package io.github.zuston.Tcos

import java.io.File

import com.google.gson.{JsonArray, JsonParser}
import com.qcloud.cos.{COSClient, ClientConfig}
import com.qcloud.cos.request.{GetFileLocalRequest, ListFolderRequest, UploadFileRequest}
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


  /**
    *
    * @param targetPathAndFileName  在 cos 中的绝对路径和文件名
    * @param bucketName
    * @param originFilePath
    */
  def upload(targetPathAndFileName: String, bucketName : String, originFilePath: String): Unit = {
    val uploadFileRequest = new UploadFileRequest(bucketName,targetPathAndFileName,originFilePath)
    val ret = cosClient.uploadFile(uploadFileRequest)
    println("upload file : "+originFilePath)
    if (revert$upload(ret)==0)  println("success") else println("error")
    println("-------------------")
  }

  /**
    * batch upload
    * @param targetPath
    * @param bucketName
    * @param originFilePaths
    */
  def batchUploadByFile(targetPath: String, bucketName : String, originFilePaths: String) : Unit = {
    val originPaths = originFilePaths.trim.split(",")
    originPaths.foreach(absolutePath => {
      val targetName = targetPath + absolutePath.split("/").last
      upload(targetName,bucketName,absolutePath)
    })
  }

  def batchUploadByFolder(targetPath: String, bucketName : String, originFolder: String) : Unit = {
    val it = getAllFiles(new File(originFolder))
    it.toList.map(_.toString).filter(x=>{
       x.split("/").last.exists(tag => tag.toString==".")
    }).foreach(temp => {
      val targetName = targetPath + temp.split("/").last
      upload(targetName,bucketName,temp)
    })
  }



  def download(bucket : String, cosPath : String, downloadPathFileName : String) : Unit = {
    val getFileLocalRequest = new GetFileLocalRequest(bucket,cosPath,downloadPathFileName)
    getFileLocalRequest.setUseCDN(false)
    val ret = cosClient.getFileLocal(getFileLocalRequest)
    if (revert$upload(ret)==0)  println("success") else println("error")
  }

  def downloadFolder(bucket: String, cosFolderPath : String, downloadPath : String) : Unit = {

  }


  def ls(bucket : String, path : String) : Unit = {
    val listFolderRequest = new ListFolderRequest(bucket,path)
    val listFolderRet = cosClient.listFolder(listFolderRequest)
    println("current bucket : "+bucket)
    println("current directory : "+path)

    val files = revert$ls(listFolderRet).map(_._1).foreach(x=>{
      x.foreach(z=>println("File : "+ z))
    })

    val folders = revert$ls(listFolderRet).map(_._2).foreach(x=>{
      x.foreach(z=>println("Folder : " + z))
    })
  }


  private def revert$ls(jsonString : String) : Option[(List[String],List[String])] = {
    val JsonObject =  new JsonParser().parse(jsonString).getAsJsonObject

    val Tcode = JsonObject.get("code").getAsInt
    val Tmsg = JsonObject.get("message").getAsString
    val Treqid = JsonObject.get("request_id").getAsString

    val dataJsonObject = JsonObject.get("data").getAsJsonObject
    val dataJsonInfos : JsonArray = dataJsonObject.get("infos").getAsJsonArray

    var listArr = new Array[String](dataJsonInfos.size())

    for ( i <- 0 until dataJsonInfos.size() ){
      val fileName = dataJsonInfos.get(i).getAsJsonObject.get("name").getAsString
      listArr(i) = fileName
    }
    val files = listArr.toList.filterNot(_.endsWith("/"))
    val folders = listArr.toList.filter(_.endsWith("/"))
    Option((files,folders))
  }

  private def revert$upload(jsonString : String) : Int = {
    val JsonObject =  new JsonParser().parse(jsonString).getAsJsonObject
    val Tcode = JsonObject.get("code").getAsInt
    Tcode
  }

  private def getAllFiles(originFolder : File) : Iterator[File] = {
    val dir = originFolder.listFiles().filter(_.isDirectory)
    val files = originFolder.listFiles().filter(_.isFile).toIterator
    files ++ dir.toIterator.flatMap(getAllFiles _)
  }

}

object  oo {
  def main(args: Array[String]): Unit = {
//    TcosClient.ls("cloud","/")
//    TcosClient.download("cloud","pom.xml","/Users/zuston/pom.xml")
//    val a = TcosClient.getAllFiles(new File("/Users/zuston/"))
    TcosClient.batchUploadByFolder("/","cloud","/Users/zuston/life/photo/")
  }
}


