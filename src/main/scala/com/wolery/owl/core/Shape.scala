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
import scala.collection.immutable.BitSet.BitSet1
import utilities.{mod,mod12,ror12}
import Shapes.info
import Shape.bit

/**
 * @param bits A 12 element bit set, each of whose bits indicates the presence
 *              or absence of the corresponding interval.
 *
 */
final class Shape private (bits: Bits,ints: Long)
{
  def name: Maybe[Name]                   = info(this).map(_.name)
  def names: Seq[Name]                    = info(this).map(_.names).getOrElse(Nil)

  def size:  ℕ                            = bitCount(bits)
  def toSet: BitSet                       = new BitSet1(bits)
  def toSeq: Seq[ℕ]                       = for (i ← 0 until size) yield at(i)

  def mode(mode: ℤ): Shape                = Shape(ror12(bits,interval(mode)))
  def modes:         Seq[Shape]           = for (i ← 0 until size) yield mode(i)

  def interval(index: ℤ): ℕ               = at(mod(index,size))

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

  def contains(interval: ℤ): Bool         = (bits & bit(interval)) != 0

  override def toString: String           = name.getOrElse(toSet.mkString("Shape(",", ",")"))
  override def equals(a: Any): Bool       = a match {case s: Shape => s.hashCode == bits; case _ => false}
  override def hashCode: Bits             = bits

  private def at(index: ℤ): ℕ =
  {
    assert(0<=index && index<size)

    (ints >>> index*4 & 0xFL).toInt
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
    assert((bits &  0x001) == 1,"zero is missing")       // Must include zero
    assert((bits & ~0xFFF) == 0,"extraneous bits")       // Must lie in range

 /* Compute the sorted interval vector 'v',  whose elements list in order the
    intervals that make up the new scale shape.  Each interval is represented
    as an integer reduced modulo 12, so fits within a 4-bit nibble, and there
    are at most 12 of them. Hence we can pack the entire vector into a single
		64 long integer...*/

    var b = bits >>> 1                                   // Copy to temporary
    var v = 0L                                           // Interval vector
    var i = 1L                                           // Interval value
    var n = 4                                            // Interval index

    while (b != 0)                                       // While bits remain
    {
      if ((b & 0x1) != 0)                                // ...lowest bit set?
      {
        v |= (i << n)                                    // ....add interval i
        n += 4                                           // ....advance index
      }

      b >>>= 1                                           // ...slide to right
      i   += 1                                           // ...tested another
    }

    assert(n == 4*bitCount(bits))                        // All intervals seen

    new Shape(bits,v)                                    // Creates the shape
  }
}

//****************************************************************************
