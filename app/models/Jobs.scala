package models

case class Job(topic: String, unique_msg_id: String, message: String, name: Option[String], timestamp: Long)


