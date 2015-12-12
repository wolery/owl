//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents a set of notes as bit set.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core;

//****************************************************************************

import java.lang.Integer._
import scala.collection.GenSet
import scala.collection.SetLike
import scala.collection.mutable.Builder
import scala.collection.generic.CanBuildFrom

import Notes._
import utilities.rol12

//****************************************************************************

final class Notes private (private val bits: Bits) extends Set[Note]
                                                      with SetLike[Note,Notes]
{
  assert((bits & ~0xFFF) == 0,"extraneous bits")

  def iterator: Iterator[Note]            = new Iterator[Note]
  {
    var i: Bits = 0

    def hasNext: Bool                     = (1 << i) <= highestOneBit(bits)

    def next(): Note =
    {
      require(hasNext);
      i = i + numberOfTrailingZeros(bits >> i) + 1
      C + i - 1
    }
  }

  def + (n: Note) : Notes                 = new Notes(bits |  bit(n))
  def - (n: Note) : Notes                 = new Notes(bits & ~bit(n))
  def \ (s: Notes): Notes                 = new Notes(bits & ~s.bits)
  def ∪ (s: Notes): Notes                 = new Notes(bits |  s.bits)
  def ∩ (s: Notes): Notes                 = new Notes(bits &  s.bits)
  def ⊖ (s: Notes): Notes                 = new Notes(bits ^  s.bits)
  def unary_~     : Notes                 = new Notes(0xFFF&  ~ bits)
  def ⊆ (s: Notes): Bool                  = (bits & ~s.bits) == 0
  def contains(n: Note): Bool             = (bits & bit(n))  != 0
  def complement       : Notes            = ~this

  override def diff(s: GenSet[Note]): Notes = s match
  {
    case s: Notes ⇒ this \ s
    case _        ⇒ super.diff(s)
  }

  override def union(s: GenSet[Note]): Notes = s match
  {
    case s: Notes ⇒ this ∪ s
    case _        ⇒ super.union(s)
  }

  override def intersect(s: GenSet[Note]): Notes = s match
  {
    case s: Notes ⇒ this ∩ s
    case _        ⇒ super.intersect(s)
  }

  override def subsetOf(s: GenSet[Note]): Bool = s match
  {
    case s: Notes ⇒ this ⊆ s
    case _        ⇒ super.subsetOf(s)
  }

  override def equals(a: Any): Bool = a match
  {
    case s: Notes ⇒ s.bits == bits
    case _        ⇒ super.equals(a)
  }

  def symdiff(s: GenSet[Note]): Notes = s match
  {
    case s: Notes ⇒ this ⊖ s
    case _        ⇒ union(s) diff intersect(s)
  }

  override def size: ℕ                    = bitCount(bits)
  override def empty: Notes               = new Notes(0)
  override def hashCode: Bits             = bits
  override def newBuilder: Builder[Note,Notes] = builder
  override def toString: String           = mkString("{",", ","}")
}

//****************************************************************************

object Notes
{
  def all: Notes                          = new Notes(0xFFF)
  def empty: Notes                        = new Notes(0)
  def apply(s: Note*)                     = (builder ++= s).result
  def apply(s: Set[Note])                 = (builder ++= s).result

  implicit
  object canBuildFrom extends CanBuildFrom[Notes,Note,Notes]
  {
    def apply()                           = builder
    def apply(s: Notes)                   = builder
  }

  implicit
  object transposing extends Transposing[Notes]
  {
    def apply(s: Notes,i: ℤ)              = new Notes(rol12(s.bits,i))
  }

  private
  def builder: Builder[Note,Notes]        = new Builder[Note,Notes]
  {
    var bits: Bits                        = 0
    def += (n: Note)                      = {bits|= bit(n); this}
    def clear()                           = {bits = 0 }
    def result()                          = new Notes(bits)
  }

  private
  def bit(n: Note): Bits                  = 1 << n-C

  private[core]
  def apply(bits: Bits): Notes            = new Notes(bits)
}

//****************************************************************************
