package sporophyte

trait Encoder { self: Configuration =>
  case class Text(content: String) extends Element {
    def encode = charset.encode(content)
  }
}
