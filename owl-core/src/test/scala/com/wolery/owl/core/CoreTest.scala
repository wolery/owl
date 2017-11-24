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

  implicit val isℤTorsor    = new RegularAction[ℤ]       // ℤ is a ℤ-torsor
  implicit val isℝTorsor    = new RegularAction[ℝ]       // ℝ is a ℝ-torsor

  test("ℤ is a ℤ-torsor")   {assertTorsor[ℤ,ℤ]()}        // Verify the axioms
  test("ℤ is Abelian")      {assertCommutative[ℤ]()}     // Verify the axioms
  test("ℝ is an ℝ-torsor")  {assertTorsor[ℝ,ℝ]()}        // Verify the axioms
  test("ℝ is Abelian")      {assertCommutative[ℝ]()}     // Verify the axioms
}

//****************************************************************************
