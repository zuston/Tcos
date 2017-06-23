package io.github.zuston.Helper

import scala.io.Source
import scala.collection.mutable.Map

/**
  * Created by zuston on 17/6/23.
  */
class PropertyHelper {

  private val props = Map[String,String]()

  def load(filePath : String) : Unit = {
    Source.fromFile(filePath).getLines()
      .filterNot(_.startsWith("#")).map(_.split("=")).foreach(loadToProps)
  }

  private def loadToProps(valueMap : Array[String]) : Unit = {

    valueMap.length match {
      case 2 => put2Map(valueMap(0),valueMap(1))
      case _ => None
    }

  }

  private def put2Map(key : String, value : String) : Unit = {
    if (key == null) {
      throw new Exception("Null Key")
    }

    if (value == null){
      throw new Exception("Null Value")
    }
    props += (key -> value)

  }

  private def getOption(key : String) : Option[String] = {
    Option(props(key))
  }

  def get(key : String) : String = {
    getOption(key).getOrElse(throw new Exception(s"null value of key is $key"))
  }

  def getInt(key : String, defaultValue : Int) : Int = {
    getOption(key).map(_.toInt).getOrElse(defaultValue)
  }

  def getDouble(key : String, defaultValue : Double) : Double = {
    getOption(key).map(_.toDouble).getOrElse(defaultValue)
  }

  def getBoolean(key : String, defaultValue : Boolean) : Boolean = {
    getOption(key).map(_.toBoolean).getOrElse(defaultValue)
  }

  def getLong(key : String, defaultValue : Long) : Long = {
    getOption(key).map(_.toLong).getOrElse(defaultValue)
  }

}


