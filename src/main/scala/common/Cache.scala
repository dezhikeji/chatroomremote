package common

import java.util
import java.util.Collections

import com.typesafe.config.ConfigFactory

/**
  * Created by isaac on 16/2/15.
  */
object Cache {
  lazy val conf = ConfigFactory.load()
  //一级缓存
  private val oneCache=Collections.synchronizedMap[String,AnyRef](new util.HashMap[String,AnyRef](conf.getInt("cache.oneSize")))
   //存储到缓存
  private def setCacheValue(key: String, v: AnyRef,time:Int)={
    val saveTime = if( time== Int.MaxValue) time else System.currentTimeMillis() / 1000 +time
    oneCache.put(key, (saveTime, v))
  }
  def getCache(key:String)={
    val now = System.currentTimeMillis() / 1000
    var cv:Tuple2[Long, AnyRef]=null
    cv = oneCache.get(key).asInstanceOf[Tuple2[Long, AnyRef]]
    if(cv!=null) {
      if (cv._1 <= now) {
        delCache(key)
        None
      } else
        Some(cv._2)
    }
    else {
        None
    }
  }

  def setCache(key: String, v: AnyRef,time:Int= -1) = {
    setCacheValue(key,v,time)
  }
  def delCache(key: String) = {
    oneCache.remove(key)
  }
}




