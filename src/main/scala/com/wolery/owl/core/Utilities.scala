//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Miscellaneous utility functions for use within the core.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

private[core]
object utilities
{
  def subscript(char: Char): Char = char match
  {
    case '-'              ⇒ '₋'
    case '+'              ⇒ '₊'
    case  c  if c.isDigit ⇒ ('₀' + c - '0').toChar
    case  c               ⇒ c
  }

  /**
   * @see  [[https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts]]
   */
  def superscript(char : Char) : Char = char match
  {
    case '1'              ⇒ '¹'
    case '2'              ⇒ '²'
    case '3'              ⇒ '³'
    case '-'              ⇒ '⁻'
    case '+'              ⇒ '⁺'
    case  c  if c.isDigit ⇒ ('⁰' + c - '0').toChar
    case  c               ⇒ c
  }

  def subscript  (s: String): String = s.map(subscript)
  def superscript(s: String): String = s.map(superscript)

/**
 * Returns the positive remainder of the integer ''i'' on division by 12.
 *
 * The result is the unique integer 0 ≤ ''r'' < 12 such that ''i'' = 12 ''q''
 * + ''r'' for some ''q'' in ℤ.
 *
 * @param i an integer
 * @see   [[https://en.wikipedia.org/wiki/Modulo_operation]]
 */
  def mod12(i: ℤ): ℤ =
  {
    val m = i % 12;

    if (m < 0) m + 12 else m
  }

/**
 * Rotates the binary representation of the integer ''i'' left by the given
 * number of bits, but as if the operation were being performed within a 12-
 * bit register.
 *
 * If ''by'' is negative, the bits are instead rotated to the right.
 *
 * The result is undefined if ''bits'' occupies more than the lowest 12 bits
 * of an integer; that is, if `bits & ~0xFFF` is non zero.
 *
 * @param  bits a 12 bit word whose bits are to be rotated left
 * @param  by   the number of bit positions to rotate ''bits'' left by
 * @return 			the result of rotating the representation of ''bits'' left by
 * 							the given number of bits
 * @see    [[https://en.wikipedia.org/wiki/Circular_shift]]
 */
  def rol12(bits: Int,by: ℤ): Int =
  {
    require((bits & ~0xFFF) == 0,"bad bits")

    val i = mod12(by)

    0xFFF & (bits << i | bits >>> 12-i)
  }

/**
 * Rotates the binary representation of the integer ''i'' right by the given
 * number of bits,  but as if the operation were being performed within a 12-
 * bit register.
 *
 * If ''by'' is negative, the bits are instead rotated to the left.
 *
 * The result is undefined if ''bits'' occupies more than the lowest 12 bits
 * of an integer; that is, if `bits & ~0xFFF` is non zero.
 *
 * @param  bits a 12 bit word whose bits are to be rotated right
 * @param  by   the number of bit positions to rotate ''bits'' right by
 * @return 			the result of rotating the representation of ''bits'' right by
 * 							the given number of bits
 * @see    [[https://en.wikipedia.org/wiki/Circular_shift]]
 */
  def ror12(bits: Int,by: ℤ): Int =
  {
    require((bits & ~0xFFF) == 0,"bad bits")

    val i = mod12(by)

    0xFFF & (bits >>> i | bits << 12-i)
  }
}

//****************************************************************************
