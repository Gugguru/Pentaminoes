package pentaminoes

import scala.util.Random

object Game {
  
  type grid = Vector[Vector[Int]]
  
  private var numberOfColors = 2
  private var currentLevel = 1
  private var currentScore = 0
  var rows = 0
  private var nextLevelLimit = 5
  
  private var firstPentamino = this.randomPentamino
  private var secondPentamino = this.randomPentamino
  
  private var _gameOn = true
  
  var grid = new Grid
  
  def newGame() = {
    this.numberOfColors = 2
    this.currentLevel = 1
    this.currentScore = 0
    this.rows = 0
    this.nextLevelLimit = 5
    
    this.firstPentamino = this.randomPentamino
    this.secondPentamino = this.randomPentamino
    
    _gameOn = true
    
    GameSounds.playMusic()
    grid.initialize()
  }
  
  /*Creates random pentamino
   *Calls Pentamino objects method: random.
   */
  def randomPentamino = {
    var randomInts = Array.fill(5)(0)
    while ( (randomInts.map(color => randomInts.count(_ == color))).reduceLeft(_ max _) >= 4 ) {
      randomInts = Array.fill(5)(Random.nextInt(this.numberOfColors)+1) // Random int 1 - numberOfColors
    }
    Pentamino.random(randomInts(0), randomInts(1), randomInts(2), randomInts(3), randomInts(4))
  }
  
  /*Places pentamino to Grid, updates points and check if game should end.
   *Calls other objects and classes methods.
   */
  def placePentamino(x: Int, y: Int) = {
    val isPlaced = grid.add(this.currentPentamino, x, y)

    if (isPlaced) {
      val pointsAndRows = grid.checkRows()
      val points = pointsAndRows._1
      val rows = pointsAndRows._2
      
      this.addScore(points)
      GameSounds.playPlacementSound()
      
      if (points > 0) {
        this.rows += rows
        while (this.isLevelUp) this.nextLevel() //Level up from level 1 to 3 is possible in theory
        GameSounds.playLineSound()
      }
      
      this.firstPentamino = this.secondPentamino
      this.secondPentamino = this.randomPentamino
    }
    
    if ( ! this.possibleMovesLeft) {
      this._gameOn = false
    }
  }

  def gameOn = this._gameOn
  
  def possibleMovesLeft = grid.checkIfMovesPossible(this.currentPentamino)
  
  // Returns true if current Pentamino can be placed to grid's coordinates (x,y)
  def canPlacePentamino(x: Int, y:Int): Boolean = {
    grid.canBePlaced(x, y)
  }
  
  def currentPentamino = this.firstPentamino
  
  def nextPentamino = this.secondPentamino
  
  // Returns 2-dimensional Vector of Ints(/colors) in grid
  def gridColors: grid = grid.colors
  
  def level = this.currentLevel
  
  //Handles level-ups.
  def nextLevel() = {
    this.currentLevel += 1
    this.numberOfColors += 1
    this.nextLevelLimit *= 2
  }
  
  //Tests if it's time to level-up.
  def isLevelUp = this.rows >= this.nextLevelLimit
  
  def score = this.currentScore
  
  def addScore(score: Int) = {
    this.currentScore += score
  }
  
  def rowsToNextLevel = this.rows + " / " + this.nextLevelLimit
  
}