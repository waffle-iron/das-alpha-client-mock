package db.client

import java.sql.Date
import javax.inject.{Inject, Singleton}

import db.DBModule
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

case class SchemeClaimRow(empref: String, userId: Long, authToken: String, validUntil: Date, refreshToken: Option[String])

trait SchemeClaimModule extends DBModule {

  import driver.api._

  val SchemeClaims = TableQuery[SchemeClaimTable]

  def forUser(userId: Long): Future[Seq[SchemeClaimRow]] = db.run(SchemeClaims.filter(_.dasUserId === userId).result)

  def forEmpref(empref: String): Future[Option[SchemeClaimRow]] = db.run(SchemeClaims.filter(_.empref === empref).result.headOption)

  def insert(cat: SchemeClaimRow): Future[Unit] = db.run(SchemeClaims += cat).map { _ => () }

  class SchemeClaimTable(tag: Tag) extends Table[SchemeClaimRow](tag, "scheme_claim") {

    def empref = column[String]("empref", O.PrimaryKey)

    def dasUserId = column[Long]("das_user_id")

    def accessToken = column[String]("access_token")

    def validUntil = column[Date]("valid_until")

    def refreshToken = column[Option[String]]("refresh_token")


    def * = (empref, dasUserId, accessToken, validUntil, refreshToken) <>(SchemeClaimRow.tupled, SchemeClaimRow.unapply)
  }

}

@Singleton
class SchemeClaimDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends SchemeClaimModule