//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

import java.lang.Integer.bitCount
import scala.collection.immutable.BitSet
import Shape.bit
import Shapes.info
import utilities.{mod,mod12,ror12}

/**
 * @param bits A 12 element bit set, each of whose bits indicates the presence
 * 					   or absence of the corresponding interval.
 *
 */
final class Shape private (bits: Bits) extends (ℤ ⇒ ℕ)
{
  def name : Maybe[Name]                  = info(this).map(_.name)
  def names: Seq[Name]                    = info(this).map(_.names).getOrElse(Nil)

  def size:  ℕ                            = bitCount(bits)
  def toSet: BitSet                       = new BitSet.BitSet1(bits)
  def toSeq: Seq[ℤ]                       = absolute

  def mode(mode: ℤ): Shape                = new Shape(ror12(bits,apply(mode)))
  def modes:         Seq[Shape]           = (0 until size).map(mode(_))

  def apply   (index: ℤ)   : ℕ            = interval(mod(index,size))

  def indexOf(interval: ℤ): Maybe[ℕ] =
  {
    val i = mod12(interval)

    if ((bits & 0x1<<i) != 0)
    {
      val m = 0xFFF >>> (11 - i)

      Some(bitCount(bits & m) - 1)
    }
    else None
  }

  def interval(index: ℤ): ℤ =
  {
    assert(0<=index && index<size)

    (ints >>> index*4 & 0xFL).toInt
  }

  def absolute: Seq[ℤ]  =
  {
    for (i ← 0 until size) yield
    {
      interval(i)
    }
  }

  def relative: Seq[ℤ]  =
  {
    var n: ℕ = 0

    for (i ← 1 until size) yield
    {
      val m = n
      n = interval(i)
      n - m
    }
  }

  def contains(interval: ℤ): Bool         = (bits & bit(interval)) != 0

  override def toString: String           = name.getOrElse(toSet.mkString("Shape(",", ",")"))
  override def equals(a: Any): Bool       = a match {case s: Shape => s.hashCode == bits; case _ => false}
  override def hashCode: Bits             = bits

  /**
   * The sequence of intervals that comprise the shape, packed into a single
   * long integer.
   */
  private var ints: Long = 0L;                           // Interval vector
  {
    var b = bits                                         // Copy the bit set
    var n = 0                                            // Intervals seen
    var i = 0L                                           // Interval to add

    while (b != 0)                                       // While bits unseen
    {
      if ((b & 0x1) != 0)                                // ...low bit is set?
      {
        ints |= (i << n*4)                               // ....add interval
        n    += 1                                        // ....seen another
      }

      b >>>= 1                                           // ...slide to right
      i   += 1                                           // ...tested another
    }

    assert(n == size)                                    // All intervals seen
  }
}

//****************************************************************************

object Shape
{
  def apply(n: Name): Maybe[Shape] = info(n).map(_.shape)

  def apply(intervals: ℤ*): Shape = intervals match
  {
    case Seq()       ⇒ {        Shape(1)}
    case Seq(0,t@_*) ⇒ {        Shape((1 /: t)((b,i) ⇒ b | bit(i)))}
    case s           ⇒ {var n=0;Shape((1 /: s)((b,i) ⇒ b | bit{n+=i;n}))}
  }

  def apply(intervals: Set[ℤ]): Shape     = Shape((1 /: intervals)((b,i) ⇒ b | bit(i)))

  implicit object transposing extends Transposing[Shape]
  {
    def apply(s: Shape,i: ℤ)              = s mode i
  }

  private
  def bit(interval: ℤ): Bits              = 1 << mod12(interval)

  private[core]
  def apply(bits: Bits): Shape =
  {
    assert((bits & ~0xFFF) == 0,"extraneous bits")
    assert((bits &  0x001) == 1,"missing zero")
    new Shape(bits)
  }
}

//****************************************************************************
