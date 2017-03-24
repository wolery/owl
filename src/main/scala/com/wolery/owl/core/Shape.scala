
//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Represents the defining interval structure of a musical scale.
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

/**
 * Represents the defining interval structure of a musical scale as a sequence
 * of intervals, modeled here as integers reduced modulo 12.
 *
 * What do the 12, say, lydian scales have in common with one another? What is
 * it that ''makes'' them lydian, exactly?
 *
 * One answer is that although each scale includes a different set of notes
 * and has a different root, the set of ''intervals'' formed by each scale's
 * notes and its root is nevertheless the same for all; this common set of
 * intervals, or ''shape'', embodies a sort of abstraction of any one specific
 * scale over its root from which all 12 scale instances can be reconstructed
 * by 'applying' the shape to a specific root; that is, by mapping its intervals,
 * which we view as transpositions, down the root note.
 *
 * A Shape, then, has a set of between 1 and 12 intervals, each an integer in
 * the range [0, .. ,11], arranged in a monotonically increasing sequence that
 * must always start with the interval 0 (the root). We may therefore speak of
 * the 'first', 'second', 'third' and so on intervals of the shape - what are
 * sometimes referred to as its ''degrees''.
 *
 * @param set A subset of the integers [0, .. ,11] encoded as an integer whose
 *            ''i''-th bit is set if and only if scale instances of this shape
 *            include the note ''i'' half steps above their root.
 *
 * @param seq A sorted sequence of the intervals listed in ''set'', encoded as
 *            as an integer whose ''i''-th  nibble is the ''i''-th interval of
 *            the sequence.
 *
 *  example   new Shape(0x555,0xA86420L)  ≡  Shape("whole tone")
 */
final class Shape private (set: Int,seq: Long) extends Serializable
{
  /**
   * The preferred name of the scale shape.
   *
   * Not every shape ''has'' a name - there are thousands of them, after all -
   * so we return the result in the Maybe monad.
   */
  def name: Maybe[Name] =
  {
    info(this).map(_.name)
  }

  /**
   * A list of names by which this shape is commonly known, beginning with the
   * preferred name.
   */
  def names: Seq[Name] =
  {
    info(this).map(_.names).getOrElse(Nil)
  }

  /**
   * The number of intervals that make up the scale shape.
   */
  def size: ℕ =
  {
    bitCount(set)
  }

  /**
   *
   */
  def toSet: BitSet =
  {
    new BitSet.BitSet1(set)
  }

  /**
   *
   */
  def toSeq: Seq[ℕ] =
  {
    for (i ← 0 until size)
      yield at(i)
  }

  /**
   *
   */
  def mode(mode: ℤ): Shape =
  {
    Shape(ror12(set,interval(mode)))
  }

  /**
   *
   */
  def modes: Seq[Shape] =
  {
    for (i ← 0 until size)
      yield mode(i)
  }

  /**
   * Returns the ''i''-th element of the underlying interval sequence.
   *
   * For a scale with this shape, this is the number of half steps between the
   * root and the ''i''-th note of the scale.
   *
   * @param  i An index into the interval sequence ''seq''.
   *
   * @return The ''i'' element of the interval sequence ''seq''.
   */
  def interval(i: ℤ): ℕ =
  {
    at(mod(i,size))
  }

  /**
   *
   */
  def indexOf(interval: ℤ): Maybe[ℕ] =
  {
    val i = mod12(interval)

    if ((set & 0x1<<i) != 0)
    {
      val m = 0xFFF >>> (11 - i)

      Some(bitCount(set & m) - 1)
    }
    else None
  }

  /**
   * Returns `true` if the given interval is included in the shape.
   *
   * @param interval
   *
   * @return `true` if
   */
  def contains(interval: ℤ): Bool =
  {
    (set & bit(interval)) != 0                           // Is i'th bit set?
  }

  /**
   *
   */
  override def toString: String =
  {
    name.getOrElse(toSet.mkString("Shape(",", ",")"))
  }

  /**
   *
   */
  override def equals(any: Any): Bool = any match
  {
    case s: Shape ⇒ s.hashCode == set
    case _        ⇒ false
  }

  /**
   *
   */
  override def hashCode: Int = set

  /**
   * Returns the ''i''-th element of the underlying interval sequence.
   *
   * For a scale with this shape, this is the number of half steps between the
   * root and ''i''-th notes of the scale.
   *
   * @param  i An index into the interval sequence ''seq''.
   *
   * @return The ''i'' element of the interval sequence ''seq''.
   */
  private
  def at(i: ℤ): ℕ =
  {
    assert(0<=i && i<size)                               // Validate argument

    (seq >>> i*4 & 0xFL).toInt                           // Subscript sequence
  }
}

/**
 * The companion object for class [[Shape]].
 */
object Shape
{
  /**
   * Retrieves a scale shape by name.
   *
   * The library includes a database of scale shapes and their associated meta
   * data (see class [[Shapes]]). Here we attempt to look up a shape by one of
   * its names, an operation which can fail, hence we return the result in the
   * [[Maybe]] monad.
   *
   * Names are matched case insensitively, and the accidental characters ♭, ♮,
   * and ♯ match the Latin letters ''b'', ''n'', and ''s'' respectively.
   *
   * @param   name A name by which the scale shape is commonly known.
   *
   * @return  The named shape, or `None` if ''name'' does not name an existing
   *           scale shape.
   *
   * @example Shape("lydian")  ≡  Some(Shape(0,2,4,6,7,9,11))
   */
  def apply(name: Name): Maybe[Shape] =
  {
    info(name).map(_.shape)
  }

  /**
   * Creates a shape from its defining set of intervals.
   *
   * Intervals are reduced modulo 12 and an interval of 0 is implied, thus the
   * expressions `Shape(Set(14))` and `Shape(Set(0,2))` both describe the same
   * scale shape.
   *
   * @param   intervals A collection of intervals defining a scale shape.
   *
   * @return  The scale shape with the given interval structure.
   *
   * @example Shape(Set(0,2,4,6,7,9,11))  ≡  Shape("lydian").get
   */
  def apply(intervals: Traversable[ℤ]): Shape =
  {
    Shape((1 /: intervals)((b,i) ⇒ b | bit(i)))
  }

  /**
   * Creates a shape from its defining set of intervals.
   *
   * The argument can take one of two forms:
   *
   *  - an ''absolute'' sequence starts at 0, each subsequent element b
   *  of the form 0, ''i,,1,,'', .. ''i,,n'',,,
   *
   *  - a ''relative'' sequence begins with some interval other than 0, each
   *  subsequent element denoting a number of half steps relative to its pre-
   *  decessor in the sequence
   *
   * Intervals are reduced modulo 12 and an interval of 0 is implied, thus the
   * expressions `Shape(14,3)` and `Shape(0,2,3)` both describe the same scale
   * shape.
   *
   * @param   intervals A collection of intervals that define the scale shape.
   *
   * @return  The scale shape with the given interval structure.
   *
   * @example Shape(0,2,4,6,7,9,11)  ≡  Shape("lydian").get // absolute
   * @example Shape(2,2,2,1,2,2)     ≡  Shape("lydian").get // relative
   */
  def apply(intervals: ℤ*): Shape = intervals match
  {
    case Seq()       ⇒ {        Shape(1)}
    case Seq(0,t@_*) ⇒ {        Shape((1 /: t)((b,i) ⇒ b | bit(i)))}
    case s           ⇒ {var n=0;Shape((1 /: s)((b,i) ⇒ b | bit{n+=i;n}))}
  }

  /**
   * Returns the singleton bitset that encodes the given inter
   * Reduces the given integer modulo 12 and encodes it as  Encodes an interval as a singleton bitset.
   *
   * @param interval  An integer representing a number of half steps above the
   *                  the root of the scale.
   *
   * @return The singleton interval bitset {''interval''}.
   */
  private
  def bit(interval: ℤ): Int =
  {
    1 << mod12(interval)
  }

  /**
   * Creates a shape from its defining set of intervals.
   *
   * This private constructor provides the underlying implementation common to
   * the public constructors above. It iterates over the bits in the specified
   * set to generate a sorted interval vector that it packs into a single long
   * integer and passes as the second parameter to the real Shape constructor.
   *
   * @param   set  A subset of the integers [0, .. ,11], encoded as an integer
   *           whose ''i''-th bit is set if and only if scale instances of this
   *           shape include the note ''i'' half steps above their root.
   *
   * @return  The scale shape with the given interval structure.
   */
  private[core]
  def apply(set: Int): Shape =
  {
    assert((set &  0x001) == 1,"zero is missing")        // Must include zero
    assert((set & ~0xFFF) == 0,"extraneous set")         // Must lie in range

 /* Compute the sorted interval vector 'v',  whose elements list in order the
    intervals that make up the new scale shape.  Each interval is represented
    as an integer reduced modulo 12, so fits within a 4-bit nibble, and there
    are at most 12 of them. Hence we can pack the entire vector into a single
    64-bit long integer...*/

    var b = set >>> 1                                    // Copy to temporary
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

    assert(n == 4*bitCount(set))                         // All intervals seen

    new Shape(set,v)                                     // Create scale shape
  }

  /**
   * The integers act upon the set of shapes via modal interchange.
   */
  implicit val transposing: Transposing[Shape] = new Transposing[Shape]
  {
    def apply(shape: Shape,i: ℤ): Shape = shape.mode(i)
  }
}

//****************************************************************************
