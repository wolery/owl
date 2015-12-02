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
import utilities.{mod,mod12,ror12}
import Shapes.info
import Shape.bit

//****************************************************************************

final class Shape private (bits: Bits) extends (ℤ ⇒ ℕ)
{
  def name : Maybe[Name]                  = info(this).map(_.name)
  def names: Seq[Name]                    = info(this).map(_.names).getOrElse(Nil)

  def size:  ℕ                            = bitCount(bits)
  def toSet: BitSet                       = new BitSet.BitSet1(bits)
  def toSeq: Seq[ℤ]                       = absolute

  def mode(mode: ℤ): Shape                = new Shape(ror12(bits,apply(mode)))
  def modes:         Seq[Shape]           = modes_imp

  def apply   (index: ℤ)   : ℕ            = interval_imp(mod(index,size))
  def indexOf (interval: ℤ): Maybe[ℕ]     = indexOf_imp(interval)
  def contains(interval: ℤ): Bool         = indexOf_imp(interval).isDefined

  override def toString: String           = name.getOrElse(toSet.mkString("Shape(",", ",")"))
  override def equals(a: Any): Bool       = a match {case s: Shape => s.hashCode == bits; case _ => false}
  override def hashCode: Bits             = bits

//****************************************************************************

  private var ints: Long                  = 0L

  {
    var (s,i,j) = (bits,0,0L)

    while (s != 0)
    {
      if ((s & 0x1) != 0)
      {
        ints |= (j << i*4)
        i    += 1
      }

      s >>>= 1
      j   += 1
    }
  }

  def absolute: Seq[ℤ]  =
  {
    for (i ← 0 until size) yield
    {
      interval_imp(i)
    }
  }

  def relative: Seq[ℤ]  =
  {
    var n: ℕ = 0

    for (i ← 1 until size) yield
    {
      val m = n
      n = interval_imp(i)
      n - m
    }
  }

  private def interval_imp(index: ℤ): ℤ =
  {
    assert(0<=index && index<size)

    (ints >>> index*4 & 0xFL).toInt
  }

  private def modes_imp: Seq[Shape] =
  {
    for (i ← 0 until size) yield
    {
      Shape(ror12(bits,interval_imp(i)))
    }
  }

  private def contains_imp(interval: ℤ): Bool =
  {
    (bits & bit(interval)) != 0
  }

  private def indexOf_imp(interval: ℤ): Maybe[ℕ] =
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
