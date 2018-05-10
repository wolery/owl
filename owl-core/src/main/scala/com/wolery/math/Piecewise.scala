//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : Header:
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery
package math

import scala.collection.Searching._
import scala.reflect.ClassTag

/**
 * Provides support for constructing functions whose definitions are specified
 * piecewise.
 *
 * By `piecewise`
 * {{{
 *
 * }}}
 *
 * @see    [[https://en.wikipedia.org/wiki/Piecewise. Piecewise (Wikipedia)]]
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
   * @see [[https://en.wikipedia.org/wiki/Step_function Step function (Wikipedia]]
   */
  final
  def step[α: Ordering: ClassTag,β: ClassTag](first: β,pieces: (α,β)*): Function[α,β] = new Function[α,β]
  {
    val (x: Array[α],f: Array[β]) =
    {
      val (x,f) = pieces.unzip

      (x.toArray,(first +: f).toArray)
    }

    def apply(a: α): β = x.search(a) match
    {
      case Found(i)          ⇒ f(i+1)
      case InsertionPoint(i) ⇒ f(i)
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
}

//****************************************************************************

object piecewise extends piecewise

//****************************************************************************
