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

import java.lang.Integer.{bitCount,numberOfTrailingZeros ⇒ ntz}
import scala.collection.immutable.BitSet
import utilities.{mod,mod12,ror12}
import Shapes.{info}

//****************************************************************************

final class Shape private (private val bits: Bits) extends AnyVal
{
  def name : Maybe[Name]                  = info(this).map(_.name)
  def aliases: Seq[Name]                  = info(this) match {case None ⇒ Nil;case Some(i) ⇒ i.aliases}

  def size:      ℕ                        = bitCount(bits)
  def intervals: BitSet                   = toSet
  def toSet:     BitSet                   = new BitSet.BitSet1(bits)
  def toSeq:     Seq[ℤ]                   = absolute

  def mode(mode: ℤ): Shape                = new Shape(ror12(bits,interval(mode)))
  def modes:         Seq[Shape]           = modes_imp

  def apply   (index: ℤ)   : ℕ            = interval_imp(index)
  def interval(index: ℤ)   : ℕ            = interval_imp(index)
  def indexOf (interval: ℤ): Maybe[ℕ]     = indexOf_imp(interval)
  def contains(interval: ℤ): Bool         = indexOf_imp(interval).isDefined

  override def toString: String           = name.getOrElse(intervals.mkString("Shape(",", ",")"))

//****************************************************************************

  def absolute: Seq[ℤ]  =
  {
    var n: ℕ = 0

    for (i ← 0 until size) yield
    {
      val m = n

      n += 1 + ntz(bits >>> n+1)

      m
    }
  }

  def relative: Seq[ℤ]  =
  {
    var n: ℕ = 0

    for (i ← 1 until size) yield
    {
      val m = n

      n += 1 + ntz(bits >>> n+1)

      n - m
    }
  }

  def interval_imp(index: ℤ): ℤ =
  {
    var i: ℕ = mod(index,size)
    var n: ℕ = 0

    while (i > 0)
    {
      n += 1 + ntz(bits >>> n+1)
      i -= 1
    }
    n
  }

  def modes_imp: Seq[Shape] =
  {
    var n: ℕ = 0

    for (i ← 0 until size) yield
    {
      val m = n

      n += 1 + ntz(bits >>> n+1)

      Shape(ror12(bits,m))
    }
  }

  def contains_imp(interval: ℤ): Bool =       (bits & Shape.bit(interval)) !=0

  def indexOf_imp(interval: ℤ): Maybe[ℕ] =
  {
    if (contains(interval))
    {
      val i = mod12(interval)
      val m = 0xFFF >>> (11 - i)

      Some(bitCount(bits & m) - 1)
    }
    else None
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
