package introgame

object testdata {
  val RustySword = Item("Rusty Sword")
  
  val TestMap = GameMap[Cell](Vector(
    Vector(
      Cell("Haunted Oak Forest", "A spooky-looking forest filled with ancient oak trees.", RustySword :: Nil),
      Cell("The Glades", "A grassy, wide open expanse that stretches for miles in every direction", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil)
    ),
    Vector(
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil)
    ),
    Vector(
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil)
    ),
    Vector(
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil)
    ),
    Vector(
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil),
      Cell("Desert", "A barren expanse of nothingness.", Nil)
    )
  ))
}