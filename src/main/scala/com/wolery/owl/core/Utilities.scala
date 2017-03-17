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

object utilities
{
  def subscript(char: Char): Char = char match
  {
    case '-'              ⇒ '₋'
    case '+'              ⇒ '₊'
    case  c if c.isDigit  ⇒ ('₀' + c - '0').toChar
    case  c               ⇒ c
  }

  /**
   * @see [[https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts Unicode subscripts and superscripts (Wikipedia)]]
   */
  def superscript(char: Char): Char = char match
  {
    case '1'              ⇒ '¹'
    case '2'              ⇒ '²'
    case '3'              ⇒ '³'
    case '-'              ⇒ '⁻'
    case '+'              ⇒ '⁺'
    case  c if c.isDigit  ⇒ ('⁰' + c - '0').toChar
    case  c               ⇒ c
  }

  def subscript  (s: String): String = s.map(subscript)
  def superscript(s: String): String = s.map(superscript)

  /**
   * Returns true if the value ''n'' lies within the closed interval [''lo'',
   * ''hi''].
   *
   * By 'closed' we mean that the interval includes its bounds.
   *
   * @param  n  The value to test for inclusion in the closed interval.
   * @param  lo The lower bound of the closed interval.
   * @param  hi The upper bound of the closed interval.
   * @return True if ''n'' lies within the closed interval [''lo'',''hi''].
   */
  def between[α: Ordering](n: α,lo: α,hi: α): Bool =
  {
    assert(lo <= hi)                                     // Validate arguments

    lo<=n && n<=hi                                       // Test for inclusion
  }

  /**
   * Returns the non-negative remainder of the integer ''i'' upon division by
   * the positive integer ''n''.
   *
   * The result is the unique integer 0 ≤ ''r'' < ''n'' such that ''i'' =
   * ''n''⋅''q'' + ''r'' for some ''q'' in ℤ.
   *
   * @param  i An integer.
   * @param  n A positive integer.
   * @return The non-negative remainder of ''i'' upon division by ''n''.
   * @see    [[https://en.wikipedia.org/wiki/Modulo_operation Modulo operation (Wikipedia)]]
   */
  def mod(i: ℤ,n: ℕ): ℕ =
  {
    assert(n > 0,"non-positive modulus")                 // Validate argument

    val r = i % n;                                       // Compute remainder

    if (r < 0) r + n else r                              // Check non-negative
  }

  /**
   * Returns the non-negative remainder of the integer ''i'' upon division by
   * 12.
   *
   * The result is the unique integer 0 ≤ ''r'' < 12 such that ''i'' = 12⋅''q''
   * + ''r'' for some ''q'' in ℤ.
   *
   * @param  i An integer.
   * @return The non-negative remainder of ''i'' upon division by 12.
   * @see    [[https://en.wikipedia.org/wiki/Modulo_operation Modulo operation (Wikipedia)]]
   */
  def mod12(i: ℤ): ℕ =
  {
    val r = i % 12;                                      // Compute remainder

    if (r < 0) r + 12 else r                             // Check non-negative
  }

  /**
   * Rotates the binary representation of a given integer left  by a specified
   * number of bits as if the operation were being performed within a register
   * exactly 12 bits wide.
   *
   * If ''by'' is negative then the bits are instead rotated to the right.
   *
   * The result is undefined if ''bits'' occupies more than just the 12 lowest
   * bits of an integer; that is, if `(bits & ~0xFFF0` is non-zero.
   *
   * For example:
   * {{{
   *    rol12(0x001, 1) = 0x002
   *    rol12(0x001,23) = 0x800
   *    rol12(0x800, 1) = 0x001
   *    rol12(0x800,-4) = 0x080
   * }}}
   *
   * @param  bits A 12 bit integer.
   * @param  by   The number of bit positions to rotate ''bits'' by.
   * @return The result of rotating ''bits'' left by ''by'' bits.
   * @see    [[https://en.wikipedia.org/wiki/Circular_shift Circular shift (Wikipedia)]]
   */
  def rol12(bits: Int,by: ℤ): Int =
  {
    assert((bits & ~0xFFF) == 0,"extraneous bits")       // Validate argument

    val i = mod12(by)                                    // Skip cycles of 12

    0xFFF & (bits << i | bits >>> 12-i)                  // Rotate, then mask
  }

  /**
   * Rotates the binary representation of a given integer right by a specified
   * number of bits as if the operation were being performed within a register
   * exactly 12 bits wide.
   *
   * If ''by'' is negative then the bits are instead rotated to the left.
   *
   * The result is undefined if ''bits'' occupies more than just the 12 lowest
   * bits of an integer; that is, if `(bits & ~0xFFF)` is non-zero.
   *
   * For example:
   * {{{
   *    ror12(0x001, 1) = 0x800
   *    ror12(0x001,25) = 0x800
   *    ror12(0x100, 1) = 0x080
   *    ror12(0x800,-4) = 0x008
   * }}}
   *
   * @param  bits A 12 bit integer.
   * @param  by   The number of bit positions to rotate ''bits'' by.
   * @return The result of rotating ''bits'' right by ''by'' bits.
   * @see    [[https://en.wikipedia.org/wiki/Circular_shift Circular shift (Wikipedia)]]
   */
  def ror12(bits: Int,by: ℤ): Int =
  {
    assert((bits & ~0xFFF) == 0,"extraneous bits")       // Validate argument

    val i = mod12(by)                                    // Skip cycles of 12

    0xFFF & (bits >>> i | bits << 12-i)                  // Rotate, then mask
  }
}

//****************************************************************************
