package gui

import pentaminoes._
import Game.grid
import scala.swing._
import scala.swing.event._
import java.awt.Color
import scala.swing.GridBagPanel._
import scala.util.Random
import javax.swing. { UIManager, ImageIcon }
import java.awt.image.BufferedImage                                           
import java.io.File                                                           
import javax.imageio.ImageIO
    

object GameWindow extends SimpleSwingApplication {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  
  private var Grid = Game.grid
  
  val gridWidth = Grid.colors(0).size
  val gridHeight = Grid.colors.size
  val blockSize = 50
  val smallBlockSize = 25
  val gridDimesnion = new Dimension(gridWidth * blockSize, gridHeight * blockSize)
  val nextGridSize = 5
  val nextGridDimension = new Dimension(nextGridSize * smallBlockSize, nextGridSize * smallBlockSize)
  val windowSize = new Dimension(1000, 900)
  
  val verticalPic = new ImageIcon("Icons/flipVertical.png")
  val horizontalPic = new ImageIcon("Icons/flipHorizontal.png")
  val clockwisePic = new ImageIcon("Icons/rotateClockwise.png")
  val counterclockwisePic = new ImageIcon("Icons/rotateCounterclockwise.png")
  val backgroundPic = ImageIO.read(new File("Icons/background.png"))
  
  val defaultFont = new Font("Castellar", 0, 30)

  val grid = new Display(gridWidth, gridHeight, Grid.colors, Grid.edges, blockSize)
  
  val currentPentamino = new Display(nextGridSize, nextGridSize, Game.currentPentamino.toVector, 
      Game.currentPentamino.twoBooleanEdges, smallBlockSize)
  
  val nextPentamino = new Display(nextGridSize, nextGridSize, Game.nextPentamino.toVector, 
      Game.nextPentamino.twoBooleanEdges, smallBlockSize)
  
  private def scoreText = "Score: " + Game.score
  private def levelText = "Level: " + Game.level
  private def rowsText = "Rows: " + Game.rowsToNextLevel
  private def updateLabels() = {
    score.text = scoreText
    level.text = levelText
    rows.text = rowsText
  }
  private def updateHighscores() = {
    val scores = Highscore.getHighscoreListAsString
    for (i <- 0 until highscores.size) {
      highscores(i).text = s"${i+1}: ${scores(i)}"
    }
  }
  
  private def updateGrids() = {
    grid.colors = Grid.colors
    grid.edges  = Grid.edges
    currentPentamino.colors = Game.currentPentamino.toVector
    currentPentamino.edges  = Game.currentPentamino.twoBooleanEdges
    nextPentamino.colors = Game.nextPentamino.toVector
    nextPentamino.edges  = Game.nextPentamino.twoBooleanEdges
  }
  
  val score = new Label{text = scoreText; preferredSize = new Dimension(200,45); font = defaultFont}
  val level = new Label{text = levelText; preferredSize = new Dimension(200,45); font = defaultFont}
  val rows  = new Label{text = rowsText;  preferredSize = new Dimension(250,45); font = defaultFont}
  
  val highscores = 
    Array.fill[Label](Highscore.getHighscoreList.size)(new Label{font = defaultFont; foreground = Color.WHITE})
  
  val scoreBoard = new FlowPanel {
    contents += score
    contents += level
    contents += rows
  }
  
  val flipHorizontally = new Button {
    preferredSize = new Dimension(100,100)
    icon = horizontalPic
    focusable = true
  }
  
  val flipVertically = new Button {
    preferredSize = new Dimension(100,100)
    icon = verticalPic
    focusable = true
  }
  
  val rotateClockwise = new Button {
    preferredSize = new Dimension(100,100)
    icon = clockwisePic
    focusable = true
  }
  
  val rotateCounterclockwise = new Button {
    preferredSize = new Dimension(100,100)
    icon = counterclockwisePic
    focusable = true
  }
  
  val playButton = new Button {
    preferredSize = new Dimension(250, 50)
    text = "Play"
    font = defaultFont
    focusable = true
  }
  
  val scoreButton = new Button {
    preferredSize = new Dimension(250,50)
    text = "Hi-Scores"
    font = defaultFont
    focusable = true
  }
  
  val menuButton = new Button {
    text = "Menu"
    font = defaultFont
    focusable = true
  }
  
  val quitButton = new Button {
    preferredSize = new Dimension(250,50)
    text = "Quit"
    font = defaultFont
    focusable = true
  }
  
  val gameScreen = new GridBagPanel {
    override def paintComponent(g: Graphics2D) = {
      g.drawImage(backgroundPic, 0, 0, null)
    }
    val c = new Constraints
    c.gridx = 0
    c.gridy = 0
    c.gridwidth = 6
    c.insets = new Insets(0,0,25,0)
    layout(scoreBoard) = c
    c.gridx = 0
    c.gridy = 1
    c.gridwidth = 3
    c.gridheight = 3
    c.insets = new Insets(0,0,0,25)
    layout(grid) = c
    c.gridwidth = 1
    c.gridheight = 1
    c.gridx = 3
    c.gridy = 1
    c.insets = new Insets(0,100,0,0)
    layout(flipHorizontally) = c
    c.gridx = 5
    c.gridy = 1
    c.insets = new Insets(0,-100,0,0)
    layout(flipVertically) = c
    c.gridx = 3
    c.gridy = 2
    c.insets = new Insets(0,0,0,0)
    layout(rotateCounterclockwise) = c
    c.gridx = 5
    c.gridy = 2
    c.insets = new Insets(0,0,0,0)
    layout(rotateClockwise) = c
    c.gridx = 4
    c.gridy = 2
    c.insets = new Insets(25,-25,0,25)
    layout(currentPentamino) = c
    c.gridx = 4
    c.gridy = 3
    c.insets = new Insets(25,-50,0,0)
    layout(nextPentamino) = c
  }

  val menuScreen = new GridBagPanel {
    override def paintComponent(g: Graphics2D) = {
      g.drawImage(backgroundPic, 0, 0, null)
    }
    val c = new Constraints
    c.insets = new Insets(13,0,13,0)
    layout(playButton) = c
    c.gridy = 2
    layout(scoreButton) = c
    c.gridy = 4
    layout(quitButton) = c
  }
  
  val highscoreScreen = new GridBagPanel {
    override def paintComponent(g: Graphics2D) = {
      g.drawImage(backgroundPic, 0, 0, null)
    }
    val c = new Constraints
    c.gridx = 0
    c.gridy = 0
    c.ipady = 25
    val scoreInfo = new Label{text = "Name, Score, Level, Rows"; font = defaultFont; foreground = Color.WHITE}
    layout(scoreInfo) = c
    c.gridy += 1
    for (score <- highscores) {
      layout(score) = c
      c.gridy += 1
    }
    layout(menuButton) = c
  }
  
  val newGame = Action("New game") { Game.newGame; updateLabels(); updateGrids(); frame.repaint() }
  
  def gameOver: Unit = {
    if (Highscore.isScoreEnough(Game.score, Game.level, Game.rows)) {
      val popup = Dialog.showInput(gameScreen, "Your score is eligible for the Highscore list!", "Highscore!", Dialog.Message.Info, initial = "Insert name")
      val name = popup.getOrElse("Anonymous").replace(' ', '_')
      Highscore.setNewScore(name, Game.score, Game.level, Game.rows)
      frame.contents = highscoreScreen
      updateHighscores()
    }
    else {
      val popup = Dialog.showConfirmation(gameScreen, "Game over! Do you want to play again?", "Game over", Dialog.Options.YesNo)
      if (popup == Dialog.Result.No) frame.contents = menuScreen
    }
    Game.newGame
    frame.repaint()
  }
  
  val frame: MainFrame = new MainFrame {
    
    title = "Pentaminoes"
    resizable = false
    location = new Point(200, 50)
    preferredSize = windowSize

    menuBar = new MenuBar {
      contents += new Menu("Game") {
        contents += new MenuItem(newGame)
      }
    }
    contents = menuScreen
    
    listenTo(grid.mouse.clicks, grid.mouse.moves, grid.keys)
    listenTo(flipHorizontally, flipVertically, rotateClockwise, rotateCounterclockwise)
    listenTo(playButton, scoreButton, menuButton, quitButton)
    reactions += {
      case MouseClicked(gameScreen, point, _, _, _)  => {
        Game.placePentamino(point.x / blockSize, point.y / blockSize)
        updateGrids()
        updateLabels()
        frame.repaint()
        if (!Game.gameOn) {
          gameOver
        }
      }
      case MouseMoved(gameScreen, point, _) => {
        //println("----------------------------------------------")
        //println(Grid.colors.mkString("\n"))
        //println("----------------------------------------------")
        val hypoGrid = Grid.hypotheticalAdd(Game.currentPentamino, point.x / blockSize, point.y / blockSize)
        grid.colors = hypoGrid.colors
        grid.edges = hypoGrid.edges
        frame.repaint()
      }
      case ButtonClicked(source) => {
        if (source == flipHorizontally) Game.currentPentamino.flipHorizontal()
        else if (source == flipVertically) Game.currentPentamino.flipVertical()
        else if (source == rotateClockwise) Game.currentPentamino.rotateClockwise()
        else if (source == rotateCounterclockwise) Game.currentPentamino.rotateCounterClockwise()
        else if (source == playButton)  {this.contents = gameScreen; Game.newGame}
        else if (source == scoreButton) {this.contents = highscoreScreen; updateHighscores()}
        else if (source == menuButton)  this.contents = menuScreen
        else if (source == quitButton)  dispose()
        updateGrids()
        frame.repaint()
      }
      case KeyPressed(_, key, _, _) => {
        println(key)
        if (key == Key.Left) Game.currentPentamino.rotateCounterClockwise()
        updateGrids()
        frame.repaint()
      }
    }
  }
  
  def top: MainFrame = frame
  
}
  
  
  
  
  