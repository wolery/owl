//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Provides support for constructing functions whose definitions
//*            are specified piecewise.
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package math

//****************************************************************************

import scala.collection.Searching._
import scala.reflect.ClassTag

import util.utilities.isIncreasing

//****************************************************************************

/**
 * Provides support for constructing functions whose definitions are specified
 * piecewise.
 *
 * By `piecewise` we mean that the domain of the function we wish to construct
 * is partitioned into a collection of mutually disjoint sub-domains, each the
 * domain of an associated sub-function, and that the union (of the graphs) of
 * these sub-functions yields the (graph of the) target function itself.
 *
 * Consider, for example, the absolute value function,  which can be specified
 * piecewise as:
 * {{{
 *            -x if x  < 0
 *    |x| =
 *             x if x >= 0
 * }}}
 * Here, the domain `ℝ` of the target function `|x|` is being expressed as the
 * union of the two sub-domains `[-∞, 0)` and `[0, ∞]`.  For all values of `x`
 * less than zero the first sub-function `x ⇒ −x` is to be used to compute the
 * result, while for all other values the second sub-function `x ⇒ x` is to be
 * used instead.
 *
 * `Step functions` form an important  subclass of piecewise-defined functions
 * in which the pieces of the target function are all constant functions.
 *
 * This trait provides functions for constructing both kinds of functions from
 * their constituent pieces, and also provides a few generally useful examples
 * of this interesting class of functions,  such as the `heaviside`, `boxcar`,
 * `abs`, and `sgn` functions.`
 *
 * @see    [[https://en.wikipedia.org/wiki/Piecewise. Piecewise (Wikipedia)]]
 * @see    [[https://en.wikipedia.org/wiki/Step_function. Step function (Wikipedia)]]
 *
 * @author Jonathon Bell
 */
trait piecewise
{
  /**
   * Constructs a function from its constituent pieces.
   *
   * The pieces are specified as a sequence of pairs `(xᵢ, fᵢ)`, where:
   * {{{
   *             f₀(x) if x ∊ [-∞, x₀)
   *    f(x) =
   *             fᵢ(x) if x ∊ [xᵢ₋₁, xᵢ)
   * }}}
   * and the values `xᵢ` occur in ascending order.
   *
   * As an example, the application `piecewise(x ⇒ -x, (0, x ⇒ x))` returns an
   * implementation of the absolute value function discussed earlier.
   *
   * @tparam α       The domain of the function to construct.
   * @tparam β       The range  of the function to construct.
   *
   * @param  f₀      The first `piece` of the function to construct.
   *                 Computes the value of the function for all arguments less
   *                 than `pieces.head._1`.
   *
   * @param  pieces  The 'pieces' of the function to construct, in the form of
   *                 a sequence of value-function pairs `(xᵢ,fᵢ)`,  where `xᵢ`
   *                 is the greatest lower bound of the sub-domain upon which
   *                 the corresponding sub-function `fᵢ` is defined.
   *
   * @return         The function whose graph is the union of the given pieces.
   *
   * @note   The values `xᵢ` must occur in ascending order.
   *
   * @see    [[https://en.wikipedia.org/wiki/Piecewise Piecewise (Wikipedia])]
   */
  final
  def piecewise[α: Ordering: ClassTag,β](f0: α ⇒ β,pieces: (α,α ⇒ β)*): Function[α,β] = new Function[α,β]
  {
    val f:α ⇒ α ⇒ β = step(f0,pieces: _*)

    def apply(a: α): β = f(a)(a)
  }

  /**
   * Constructs a step function from its constituent steps.
   *
   * The steps are specified as a sequence of pairs `(xᵢ, yᵢ)`, where:
   * {{{
   *             y₀  if x ∊ [-∞, x₀)
   *    f(x) =
   *             yᵢ  if x ∊ [xᵢᵢ₋₁, xᵢ)
   * }}}
   * and the values `xᵢ` occur in ascending order.
   *
   * As an example, the application `step(0, (0,1))` returns an implementation
   * of the `heaviside` function discussed earlier.
   *
   * @tparam α       The domain of the function to construct.
   * @tparam β       The range  of the function to construct.
   *
   * @param  y₀      The first `piece` of the function to construct.
   *                 Specifies value of the function for all values less than
   *                 `pieces.head._1`.
   *
   * @param  pieces  The 'pieces' of the function to construct, in the form of
   *                 a sequence of pairs `(xᵢ,yᵢ)`, where `xᵢ` is the greatest
   *                 lower bound of the sub-domain for which `yᵢ` is the value
   *                 of the target function being defined.
   *
   * @return         The function whose graph is the union of the given pieces.
   *
   * @see [[https://en.wikipedia.org/wiki/Step_function Step function (Wikipedia)]]
   */
  final
  def step[α: Ordering: ClassTag,β: ClassTag](y0: β,pieces: (α,β)*): Function[α,β] = new Function[α,β]
  {
    val (d: Array[α],r: Array[β]) =                      // Sub-domains/ranges
    {
      val (d,r) = pieces.unzip                           // ...peel them apart

      assert(isIncreasing(d:_*))                         // ...validate domain

      (d.toArray,(y0 +: r).toArray)                      // ...cache as arrays
    }

    def apply(a: α): β = d.search(a) match               // Find the subdomain
    {
      case Found(i)          ⇒ r(i+1)                    // ...select constant
      case InsertionPoint(i) ⇒ r(i)                      // ...select constant
    }
  }

  /**
   * Returns the function that is zero over the entire real line, except for a
   * single interval `[a, b]` where it takes the constant value `y`.
   *
   * @param  a  The lower bound of the interval on which the result is `y`.
   * @param  b  The upper bound of the interval on which the result is `y`.
   * @param  y  The value of the function on the interval `[a, b]`.
   * @param  x  Any real number.
   *
   * @return `y` when `x` ∊ `[a, b]`, and `0` otherwise.
   *
   * @see [[https://en.wikipedia.org/wiki/Boxcar_function Boxcar function (Wikipedia)]]
   */
  final
  def boxcar(a: ℝ,b: ℝ,y: ℝ)(x: ℝ): ℝ =
  {
    if (x.isBetween(a,b)) y else 0
  }

  /**
   * Returns zero for a negative argument and one otherwise.
   *
   * @param  x  Any real number.
   *
   * @return `0` when `x` is negative, and `1` otherwise.
   *
   * @see [[https://en.wikipedia.org/wiki/Heavisde_function Heaviside function (Wikipedia)]]
   */
  final
  def heaviside(x: ℝ): ℤ =
  {
    if (x < 0) 0 else 1
  }

  /**
   * Returns the `absolute value` or `modulus` `|x|` of the real number `x`.
   *
   * @param  x Any real number.
   *
   * @return `-x` when `x` is negative, and `x` otherwise.
   *
   * @see    [[https://en.wikipedia.org/wiki/Absolute_value Absolute value (Wikipedia)]
   */
  final
  def abs(x: ℝ): ℝ =
  {
    if (x < 0) -x else x
  }

  /**
   * Returns the `sign` of the given real number.
   *
   * @param  x  Any real number
   *
   * @return `-1` when `x `is negative, `+1` when `x` is positive, and `0`
   *         otherwise.
   *
   * @see [[https://en.wikipedia.org/wiki/Sign_function Sign function (Wikipedia)]]
   */
  final
  def sgn(x: ℝ): ℤ =
  {
    if (x < 0) -1 else
    if (x > 0)  1 else 0
  }
}

/**
 * Provides support for constructing functions whose definitions are specified
 * piecewise.
 *
 * @author Jonathon Bell
 */
object piecewise extends piecewise

//****************************************************************************
