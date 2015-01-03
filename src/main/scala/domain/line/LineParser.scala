package domain.line

import commons.StringUtils._

trait LineParser {
  this:LineParserStrategyComponent =>

  def fromLine: (String) => Option[Line] = {
    case line if line.isNullOrEmpty => None
    case line =>
      strategies.toStream.flatMap (s => s._2(line)).headOption
  }
}

trait LineParserComponent {
  val parser = new LineParser with LineParserStrategyComponent
}
