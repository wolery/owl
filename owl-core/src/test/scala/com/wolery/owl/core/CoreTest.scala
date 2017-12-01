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
  import arbitrary._                                     // For Owl implicits

  implicit val z =          `Group[ℤ]`.regularAction     // ℤ is a ℤ-torsor
  implicit val r =          `Group[ℝ]`.regularAction     // ℝ is a ℝ-torsor

  test("ℤ is a ℤ-torsor")   {isTorsor[ℤ,ℤ]()}            // Verify the axioms
  test("ℤ is commutative")  {isCommutative[ℤ]()}         // Verify the axioms
  test("ℝ is an ℝ-torsor")  {isTorsor[ℝ,ℝ]()}            // Verify the axioms
  test("ℝ is commutative")  {isCommutative[ℝ]()}         // Verify the axioms
}

//****************************************************************************
