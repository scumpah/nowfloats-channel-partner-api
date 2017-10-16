package database

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.inject.Inject
import models._
import play.api.Logger
import play.api.db.slick._
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import scala.concurrent._
import scala.concurrent.duration.Duration


/**
  * Created by raghav on 15/10/17.
  */


class MYSQLDao @Inject()(
                          protected val dbConfigProvider: DatabaseConfigProvider
                        ) (implicit val executionContext: ExecutionContext) extends
  HasDatabaseConfigProvider[JdbcProfile]{

  def storeUserDetails(userTransactionInfo: UserTransaction): Unit = {
    try{
    val userTransactionInfo1 = UserTransactionInfo(userTransactionInfo.UUID,userTransactionInfo.channelId,userTransactionInfo.channel,userTransactionInfo.channelUserDataType,userTransactionInfo.channelUserDataValue,userTransactionInfo.channelUserDataMetaData,userTransactionInfo.mobileNumber,userTransactionInfo.emailId,userTransactionInfo.addedDate,userTransactionInfo.transactionStatus, userTransactionInfo.updatedDate)
    val insert = UserTransactionInfoRows returning UserTransactionInfoRows.map(_.transactionId)
    val insertQuery = insert into ((userTransactionInfo1, id) => userTransactionInfo1.copy(transactionId = id))
    val action = insertQuery += userTransactionInfo1
    val res = db.run(action)
    val t = Await.result(res, Duration.Inf)
    Logger.debug(s"create cc user info result $t")
  }
    catch {
      case ex:Exception =>
        Logger.info(s"error in db ${db.toString}")
        Logger.error(s"error in storing transaction info ${ex.getMessage}")
    }
  }





  private class UserTran(tag: Tag) extends Table[UserTransactionInfo](tag, "TRANSACTION_DETAILS") {
    implicit val timestampAColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
      { dt ⇒ java.sql.Timestamp.valueOf(dt) }, { ts ⇒ ts.toLocalDateTime }
    )
    def transactionId = column[Int]("TRANSACTION_ID", O.PrimaryKey, O.AutoInc)

    def UUID = column[String]("UUID")

    def channelId = column[String]("CHANNEL_ID")

    def channel = column[String]("CHANNEL")

    def channelUserDataType = column[String]("CHANNEL_USER_DATA_TYPE")

    def channelUserDataValue = column[String]("CHANNEL_USER_DATA_VALUE")

    def channelUserDataMetaData = column[String]("CHANNEL_USER_DATA_METADATA")

    def mobileNumber = column[String]("MOBILE_NUMBER")

    def emailId = column[String]("EMAIL_ID")

    def addedDate = column[LocalDateTime]("ADDED_DATE")

    def updatedDate = column[LocalDateTime]("UPDATED_DATE")

    def transactionStatus = column[String]("TRANSACTION_STATUS")

    def * = (UUID, channelId, channel, channelUserDataType, channelUserDataValue, channelUserDataMetaData, mobileNumber, emailId, addedDate, transactionStatus, updatedDate, transactionId)  <> ((UserTransactionInfo.apply _).tupled, UserTransactionInfo.unapply)
  }

  private val UserTransactionInfoRows = TableQuery[UserTran]
}

