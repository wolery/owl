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

import scala.collection.Searching._
import scala.reflect.ClassTag

import util.utilities.isIncreasing

/**
 * Provides support for constructing functions whose definitions are specified
 * piecewise.
 *
 * By `piecewise` we mean that the domain of the function we wish to construct
 * is partitioned into a collection of mutually disjoint sub-domains, each the
 * domain of an associated sub-function, and that the union (of the graphs) of
 * these sub-functions yields the target function itself.
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
 * the result while for values of `x` greater than or equal to zero the second
 * sub-function `x ⇒ x` is to be used instead.
 *
 * 'Step functions' represent an important special case in which the pieces of
 * the target function are all constant functions.
 *
 * @see    [[https://en.wikipedia.org/wiki/Piecewise. Piecewise (Wikipedia)]]
 * @see    [[https://en.wikipedia.org/wiki/Step_function. Step function (Wikipedia)]]
 *
 * @author Jonathon Bell
 */
trait piecewise
{
  /**
   * Returns the function whise
   *
   * @tparam α       The domain of the function to construct.
   * @tparam β       The range  of the function to construct.
   *
   * @param  first   The first `piece` of the function to construct.
   *
   *                 Computes the value of the function for all arguments less
   *                 than `pieces.head._1`.
   *
   * @param  pieces  The 'pieces' of the function to construct, in the form of
   *                 a sequence of value-function pairs `(x,f)`,  where `x` is
   *                 the greatest lower bound of the sub-domain for which `f`
   *                 is defined.
   *
   * @return         The
   *
   * @see    [[https://en.wikipedia.org/wiki/Piecewise Piecewise (Wikipedia]]
   */
  final
  def piecewise[α: Ordering: ClassTag,β](first: α ⇒ β,pieces: (α,α ⇒ β)*): Function[α,β] = new Function[α,β]
  {
    val f:α ⇒ α ⇒ β = step(first,pieces: _*)

    def apply(a: α): β = f(a)(a)
  }

  /**
   * Constructs a step function from its constituent steps.
   *
   * The steps are specified as a sequence of pairs `(xᵢ, yᵢ)`, where:
   * {{{
   *             y₀      if x in [-∞, x₀)
   *    f(x) =
   *             y,,i,,  if x in [xᵢ, yᵢ)
   * }}}
   *
   *
   *
   *
   * @tparam α       The domain of the function to construct.
   * @tparam β       The range  of the function to construct.
   *
   * @param  y₀      The first `piece` of the function to construct.
   *
   *                 Specifies value of the function for all arguments less
   *                 than `pieces.head._1`.
   *
   * @param  pieces  The 'pieces' of the function to construct, in the form of
   *                 a sequence of pairs `(xᵢ,yᵢ)`, where `xᵢ` is the greatest
   *                 lower bound of the sub-domain for which `yᵢ` is the value
   *                 of the target function being defined.
   *
   * @return         The function whose graph is the union of the given pieces.
   *
   * @see [[https://en.wikipedia.org/wiki/Step_function Step function (Wikipedia]]
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
   * @param  lo
   * @param  hi
   * @param  y
   * @param  x
   *
   * @return
   *
   * @see [[https://en.wikipedia.org/wiki/Boxcar_function Boxcar function (Wikipedia]]
   */
  final
  def boxcar(lo: ℝ,hi: ℝ,y: ℝ)(x: ℝ): ℝ =
  {
    if (x.isBetween(lo,hi)) y else 0
  }

  /**
   * @param  x
   *
   * @return
   *
   * @see [[https://en.wikipedia.org/wiki/Heavisde_function Heaviside function (Wikipedia]]
   */
  final
  def heaviside(x: ℝ): ℤ =
  {
    if (x < 0) 0 else 1
  }

  /**
   * @param  x
   *
   * @return
   *
   * @see [[https://en.wikipedia.org/wiki/Sign_function Sign function (Wikipedia]]
   */
  final
  def sign(x: ℝ): ℤ =
  {
    if (x < 0) -1 else
    if (x > 0)  1 else 0
  }

  /**
   * Returns
   *
   * @param  x Any real number.
   *
   * @return The absolute value of the given real number.
   *
   * @see    [[https://en.wikipedia.org/wiki/Absolute_value Absolute value (Wikipedia)]
   */
  final
  def abs(x: ℝ): ℝ =
  {
    if (x < 0) -x else x
  }
}

//****************************************************************************

object piecewise extends piecewise

//****************************************************************************
