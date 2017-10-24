//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Miscellaneous utility functions used throughout Owl.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package util

/**
 * Miscellaneous utility functions used throughout Owl.
 *
 * @author Jonathon Bell
 */
trait utilities
{
  /**
   * Returns a subscripted version of the given character ''c'' if such a code
   * point is present in the Unicode standard, and ''c'' otherwise.
   *
   * Subscripted versions exist for the full set of Arabic numerals.
   *
   * @param  c  An arbitrary character.
   *
   * @return The subscripted version of ''c'', if defined by Unicode, otherwise ''c''.
   * @see    [[https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts Unicode subscripts and superscripts (Wikipedia)]]
   */
  def subscript(c: Char): Char = c match
  {
    case '-'              ⇒ '₋'
    case '+'              ⇒ '₊'
    case '='              ⇒ '₌'
    case '('              ⇒ '₍'
    case ')'              ⇒ '₎'
    case  c if c.isDigit  ⇒ ('₀' + c - '0').toChar
    case  _               ⇒ c
  }

  /**
   * Returns a superscripted version of the given character ''c'' if such a code
   * point is present in the Unicode standard, and ''c'' otherwise.
   *
   * Superscripted versions exist for the full set of Arabic numerals.
   *
   * @param  c  An arbitrary character.
   *
   * @return The superscripted version of the character ''c'', if defined by Unicode, otherwise ''c''.
   * @see    [[https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts Unicode subscripts and superscripts (Wikipedia)]]
   */
  def superscript(c: Char): Char = c match
  {
    case '1'              ⇒ '¹'
    case '2'              ⇒ '²'
    case '3'              ⇒ '³'
    case '-'              ⇒ '⁻'
    case '+'              ⇒ '⁺'
    case '='              ⇒ '⁼'
    case '('              ⇒ '⁽'
    case ')'              ⇒ '⁾'
    case  c if c.isDigit  ⇒ ('⁰' + c - '0').toChar
    case  _               ⇒ c
  }

  /**
   * Returns a copy of the given string in which  characters with  subscripted
   * versions defined by Unicode are replaced with subscripted variants.
   *
   * For example:
   * {{{
   *    subscript("A13")  =  "A₁₃"
   * }}}
   *
   * @param  s  A string of characters.
   *
   * @return A superscripted version of the character string ''s''.
   */
  def subscript(s: String): String = s.map(subscript)

  /**
   * Returns a copy of the given string in which characters with superscripted
   * versions defined by Unicode are replaced with superscripted variants.
   *
   * For example:
   * {{{
   *    superscript("A13")  =  "A¹³"
   * }}}
   *
   * @param  s A string of characters.
   *
   * @return A superscripted version of the character string ''s''.
   */
  def superscript(s: String): String = s.map(superscript)

  /**
   * Returns true if the value ''v'' lies within the closed interval [''lo'',
   * ''hi''].
   *
   * By 'closed' we mean that the interval includes its bounds.
   *
   * @param  v   The value to test for inclusion in the closed interval.
   * @param  lo  The lower bound of the closed interval.
   * @param  hi  The upper bound of the closed interval.
   *
   * @return True if ''v'' lies within the closed interval [''lo'',''hi''].
   */
  def isBetween[α: Ordering](v: α,lo: α,hi: α): Bool =
  {
    assert(lo <= hi)                                     // Validate arguments

    lo<=v && v<=hi                                       // Test for inclusion
  }

  /**
   * Returns true if the integer ''i'' is a natural power of 2.
   *
   * @param  i  An arbitrary integer.
   *
   * @return True if the integer ''i'' is a natural power of 2.
   */
  def isPowerOf2(i: ℤ): Bool =
  {
    i > 0 && (i & (i-1)) == 0                            // Mask off with i-1
  }

  /**
   * Clamp the given value to lie within the closed interval [''lo'', ''hi''].
   *
   * @param lo  The lower bound of the range to which ''v'' is clamped.
   * @param v   The value to be clamped.
   * @param hi  The upper bound of the range to which ''v'' is clamped.
   *
   * @return The value ''v'', clamped to lie within the closed interval
   *         [''lo'', ''hi''].
   */
  def clamp[α: Ordering](lo: α,v: α,hi: α): α =
  {
    assert(lo <= hi);                                    // Validate arguments

    if (v < lo)                                          // Less than 'lo'?
      lo                                                 // ...clamp to 'lo'
    else
    if (v > hi)                                          // Greater than 'hi'?
      hi                                                 // ...clamp to 'hi'
    else                                                 // Within interval
      v                                                  // ...nothing to do
  }

  /**
   * Returns the non-negative remainder of the integer ''i'' upon division by
   * the positive integer ''n''.
   *
   * The result is the unique integer 0 ≤ ''r'' < ''n'' such that ''i'' =
   * ''n''⋅''q'' + ''r'' for some ''q'' in ℤ.
   *
   * @param  i  An integer.
   * @param  n  A positive integer.
   *
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
   * @param  i  An integer.
   *
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
   * @param  bits  A 12 bit integer.
   * @param  by    The number of bit positions to rotate ''bits'' by.
   *
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
   * @param  bits  A 12 bit integer.
   * @param  by    The number of bit positions to rotate ''bits'' by.
   *
   * @return The result of rotating ''bits'' right by ''by'' bits.
   * @see    [[https://en.wikipedia.org/wiki/Circular_shift Circular shift (Wikipedia)]]
   */
  def ror12(bits: Int,by: ℤ): Int =
  {
    assert((bits & ~0xFFF) == 0,"extraneous bits")       // Validate argument

    val i = mod12(by)                                    // Skip cycles of 12

    0xFFF & (bits >>> i | bits << 12-i)                  // Rotate, then mask
  }

  /**
    * Emits an audible 'beep' that depends upon the native system settings and
    * hardware capabilities.
    */
  def beep(): Unit =
  {
    java.awt.Toolkit.getDefaultToolkit.beep()            // Delegate to AWT
  }
}

/**
 * Miscellaneous utility functions used throughout Owl.
 *
 * @author Jonathon Bell
 */
object utilities extends utilities

//****************************************************************************
