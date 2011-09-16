/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2009-2010, Jesse Eichar             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scalaio.test.stream

import scalaio.test._
import java.io.ByteArrayInputStream
import scalax.io.{Codec, Resource}
import Resource._
import org.junit.Test
import org.junit.Assert._
import java.lang.String

class InputTest extends AbstractInputTests with DataIndependentLongTraversableTest[Byte]  {
    def independentTraversable = {
    val data = (1 to 100).map(_ => '1').mkString
    val size = data.getBytes(Codec.UTF8.charSet).size
    val resource = input(TextCustomData("",data))
    resource.bytes
  }
  val sample = Array[Byte](1,3,4)
  val independentExpectedData:Seq[Byte] = (1 to 100).map(_ => 49.toByte)
  def times(t1:Byte, t2:Byte):Byte = (t1 * t2).toByte
  def lessThan(t:Byte,i:Int):Boolean = t < i
  def scanSeed = 2.toByte
  protected def stringBasedStream(sep: String) =
    new java.io.ByteArrayInputStream(text(sep))
  protected def text(sep: String) = {
    val finalText: String = Constants.TEXT_VALUE.replaceAll("""\n""", sep)
    finalText.getBytes(Codec.UTF8.charSet)
  }

  protected def input(t: Type) = t match {
    case t@TextNewLine => fromInputStream(stringBasedStream(t.sep))
    case t@TextPair => fromInputStream(stringBasedStream(t.sep))
    case t@TextCarriageReturn => fromInputStream(stringBasedStream(t.sep))
    case TextCustom(sep) => fromInputStream(stringBasedStream(sep))
    case TextCustomData(sep, data) => fromInputStream(
      new ByteArrayInputStream(data.getBytes(Codec.UTF8.charSet))
    )
    case Image => fromInputStream(Constants.IMAGE.openStream())
  }

  override protected def sizeIsDefined = false

  @Test
  def issue_8_lines_in_Input_not_lazy {
    import scalax.io.Line.Terminators._

    val file = largeResource(Key.TEXT)
    
    val start = System.currentTimeMillis
    val fromFile = Resource.fromFile(file).lines(NewLine)(codec=Codec.UTF8)
    val fromString = Resource.fromFile(file.getAbsolutePath).lines(NewLine)(codec=Codec.UTF8)
    fromString.toString
    fromFile.toString
    val end = System.currentTimeMillis
    assertTrue(end-start < 500)
  }

}

