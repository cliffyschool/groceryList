package domain

package object line {
  type Strategy = (String, (String) => Option[Line])
}
