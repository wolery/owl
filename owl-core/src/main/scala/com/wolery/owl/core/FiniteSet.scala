//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : A specialized implementation of Set[α] for finite types α.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl
package core

import scala.collection.{GenSet,SetLike}
import scala.collection.mutable.{Builder,BitSet ⇒ mBitSet}
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.BitSet

import FiniteSet._

/**
 * A specialized implementation of `Set[α]` for finite types `α`.
 *
 * @tparam α  An instance of the type class `Finite`.
 *
 * @author Jonathon Bell
 */
final
class FiniteSet[α] private (private val m_imp: BitSet)(implicit ε: Finite[α])
extends Set[α] with SetLike[α,FiniteSet[α]]
{
  assert(m_imp.isEmpty || m_imp.last<ε.size)

  def iterator: Iterator[α] = new Iterator[α]
  {
    val i             = m_imp.iterator
    def hasNext: Bool = i.hasNext
    def next() : α    = ε.fromℕ(i.next())
  }

  def + (a: α)           : FiniteSet[α]  = FiniteSet(m_imp +  ε.toℕ(a))
  def - (a: α)           : FiniteSet[α]  = FiniteSet(m_imp -  ε.toℕ(a))
  def \ (s: FiniteSet[α]): FiniteSet[α]  = FiniteSet(m_imp &~ s.m_imp)
  def ∪ (s: FiniteSet[α]): FiniteSet[α]  = FiniteSet(m_imp |  s.m_imp)
  def ∩ (s: FiniteSet[α]): FiniteSet[α]  = FiniteSet(m_imp &  s.m_imp)
  def ⊖ (s: FiniteSet[α]): FiniteSet[α]  = FiniteSet(m_imp ^  s.m_imp)
  def unary_~            : FiniteSet[α]  = FiniteSet(full.m_imp &~ m_imp)
  def ⊆ (s: FiniteSet[α]): Bool          = m_imp.subsetOf(s.m_imp)
  def contains(a: α)     : Bool          = m_imp.contains(ε.toℕ(a))
  def complement         : FiniteSet[α]  = ~this

  override def diff(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this \ s
    case _               ⇒ super.diff(s)
  }

  override def union(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this ∪ s
    case _               ⇒ super.union(s)
  }

  override def intersect(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this ∩ s
    case _               ⇒ super.intersect(s)
  }

  override def subsetOf(s: GenSet[α]): Bool = s match
  {
    case s: FiniteSet[α] ⇒ this ⊆ s
    case _               ⇒ super.subsetOf(s)
  }

  override def equals(a: Any): Bool = a match
  {
    case s: FiniteSet[α] ⇒ this.m_imp equals s.m_imp
    case _               ⇒ super.equals(a)
  }

  def symdiff(s: GenSet[α]): FiniteSet[α] = s match
  {
    case s: FiniteSet[α] ⇒ this ⊖ s
    case _               ⇒ union(s) diff intersect(s)
  }

  override def size: ℕ                            = m_imp.size
  override def empty: FiniteSet[α]                = FiniteSet()
  override def hashCode: ℕ                        = m_imp.hashCode
  override def newBuilder: Builder[α,FiniteSet[α]]= builder[α]
  override def toString: String                   = mkString("{",", ","}")
}

/**
 * The companion object for class [[FiniteSet]].
 *
 * @author Jonathon Bell
 */
object FiniteSet
{
  def full[α](implicit ε: Finite[α]): FiniteSet[α] =
  {
    val n = ε.size >> 6               // size / 64
    val a = Array.fill(1 + n)(~0L)
    a(n) &= (1 << (ε.size & 63)) - 1  // size % 64
    FiniteSet(a)
  }

  def empty[α: Finite]                   : FiniteSet[α] = FiniteSet(BitSet())

  def apply[α: Finite](s: α*)            : FiniteSet[α] = (builder[α] ++= s).result

  def apply[α: Finite](s: Traversable[α]): FiniteSet[α] = (builder[α] ++= s).result

  implicit
  def canBuildFrom[α: Finite] = new CanBuildFrom[Set[_],α,FiniteSet[α]]
  {
    def apply()            = builder[α]
    def apply(s: Set[_])   = builder[α]
  }

  private
  def builder[α](implicit ε: Finite[α]): Builder[α,FiniteSet[α]] = new Builder[α,FiniteSet[α]]
  {
    val m_imp = mBitSet()                                //

    override def +=(n: α)  = {m_imp += ε.toℕ(n); this}
    override def clear()   = m_imp.clear()
    override def result()  = FiniteSet(m_imp.toBitMask)
  }

  @inline private
  def apply[α: Finite](bitset: BitSet): FiniteSet[α] =
  {
    new FiniteSet[α](bitset)
  }

  @inline private[core]
  def apply[α: Finite](mask: Array[Long]): FiniteSet[α] =
  {
    new FiniteSet[α](BitSet.fromBitMaskNoCopy(mask))
  }
}

//****************************************************************************
