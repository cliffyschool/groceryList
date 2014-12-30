package domain.line.parse.strategies

import domain.line.Line

/**
 * Created by cfreeman on 12/29/14.
 */
trait ParseStrategyTest {
  def parse: (String) => Option[Line]
}
