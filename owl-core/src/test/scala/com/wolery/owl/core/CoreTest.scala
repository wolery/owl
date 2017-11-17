//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : Unit tests for the core package.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package core

//****************************************************************************

class CoreTest extends CoreSuite
{
 /* ℝ only satisfies the axioms exactly for limited subsets of its values due
    to the inherent limitations of floating point representation. We are only
    interested in audible frequencies, however, so restrict the set of values
    that we test for rather than thread a  custom comparison function through
    the entire test suite...*/

  implicit val r = Arbitrary(generate.real)              // For r ∈ [-128,128]

  test("ℤ is a ℤ-torsor")  {isTorsor[ℤ,ℤ]()}             // Verify the axioms
  test("ℤ is abelian")     {isCommutative[ℤ]()}          // Verify the axioms
  test("ℝ is an ℝ-torsor") {isTorsor[ℝ,ℝ]()}             // Verify the axioms
  test("ℝ is abelian")     {isCommutative[ℝ]()}          // Verify the axioms
}

//****************************************************************************
