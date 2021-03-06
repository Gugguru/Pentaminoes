package pentaminoes

import scala.io.Source
import java.io.PrintWriter
import scala.collection.mutable.Buffer


object Highscore {
  
  private val fileName = "highscores.txt"
  private val listLenght = 10
  private var highscoreLists = this.readListFromFile
  private var highscoreList = this.highscoreLists._1
  private var highscoreListAsOptionString = this.highscoreLists._2
  private var minScore = this.minimumScore(this.highscoreList)
  
  def initialize() = {
    this.writeListToFile(Vector())
  }
  
  def getHighscoreList = {
    this.highscoreList
  }
  
  def getHighscoreListAsOptionString = {
    this.highscoreListAsOptionString
  }
  
  def getHighscoreListAsString = {
    this.highscoreListAsOptionString.map(_.getOrElse("----- 0 0 0"))
  }
  
  /*New score is saved and the file is updated.
   *Returns the new scores position in the list (int 0-9 or 10 if score wasn't enough).
   */
  def setNewScore(name: String, score: Int, level: Int, rows: Int): Int = {
    val oldList = this.getHighscoreList
    val newList = oldList.toBuffer
    newList += Some(Tuple4(name, score, level, rows))
    val newListVector = this.arrange(newList.toVector).take(this.listLenght)
    this.writeListToFile(newListVector)
    this.findPosition(name, score, level, rows)-1
  }
  
  //Tests if score is enough to get to the highscorelist.
  def isScoreEnough(score: Int, level: Int, rows: Int): Boolean = {
    this.arrange(Vector(Option(this.minScore), Option(Tuple4("",score,level,rows))))(1) != Some(Tuple4("",score,level,rows))
  }
  
  /*Reads highscoredata from file.
   *Returns highscores as vector[String] and as vector[option[string]].
   */
  private def readListFromFile: Tuple2[Vector[Option[Tuple4[String,Int,Int,Int]]], Vector[Option[String]]] = {
    val file = Source.fromFile(fileName)
    val highscores = Buffer[Option[Tuple4[String,Int,Int,Int]]]()
    val highscoresAsOptionString = Buffer[Option[String]]()
    
    try {
      for (line <- file.getLines().take(listLenght)) {
        val data = line.split(" ")
        try { highscores += Some(Tuple4(data(0), data(1).toInt, data(2).toInt, data(3).toInt)) }
        catch { case ex: Exception => highscores += None }
        highscoresAsOptionString += Some(line)
      }
    } finally {
      for (i <- highscores.length until this.listLenght) {
        highscores += None
        highscoresAsOptionString += None
      }
      file.close()
    }
    Tuple2(highscores.toVector, highscoresAsOptionString.toVector)
  }
  
  //Writes data to highscore file and updates local variables to match files new data.
  private def writeListToFile(list: Vector[Option[Tuple4[String,Int,Int,Int]]]) = {
    val file = new PrintWriter(fileName)
    for (row <- list) {
      if (row.isDefined) file.println(row.get._1 + " " + row.get._2 + " " + row.get._3 + " " + row.get._4)
    }
    file.close()
    this.updateVariables()
  }
  
  //Gets the position of someone on the highscore list.
  private def findPosition(name: String, score: Int, level: Int, rows: Int): Int = {
    this.listLenght - this.highscoreList.reverse.indexOf(Some(Tuple4(name, score, level, rows)))
  } 
  
  private def updateVariables() = {
    highscoreLists = this.readListFromFile
    highscoreList = highscoreLists._1
    highscoreListAsOptionString = highscoreLists._2
    minScore = this.minimumScore(this.highscoreList)
  }
  
  //Gets the smallest score in the highscore list.
  private def minimumScore(list: Vector[Option[Tuple4[String,Int,Int,Int]]]) = {
    list.last.getOrElse(Tuple4("",0,0,0))
  }
  
  /*Arrange given list in descending order.
   *If score is same, level and row are tiebreakers.
   *If level and rows also are same, the one which was on the list first gets higher position.
   */
  private def arrange(list: Vector[Option[Tuple4[String,Int,Int,Int]]]): Vector[Option[Tuple4[String,Int,Int,Int]]] = {
    val arrangedList = list.map(_.getOrElse(Tuple4("",0,0,0))).sortWith(_._4 > _._4).sortWith(_._3 > _._3).sortWith(_._2 > _._2)
    arrangedList.map({tuple: Tuple4[String,Int,Int,Int] => if (tuple == ("",0,0,0)) None; else Some(tuple) })
  }
  
}