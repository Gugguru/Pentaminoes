package gui
import scala.swing.{ GridBagPanel, Graphics2D }
import javax.swing.ImageIcon

class Screen extends GridBagPanel {
  
  private val backgroundPic = new ImageIcon("Icons/background.png")
  
  override def paintComponent(g: Graphics2D) = {
    g.drawImage(backgroundPic.getImage, 0, 0, null)
  }
  
}