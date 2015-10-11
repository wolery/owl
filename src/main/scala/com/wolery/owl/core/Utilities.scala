//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 3 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

import Math.{log,pow,floorMod}

//****************************************************************************

object utilities
{
   def subscript(char : Char) : Char = char match
   {
      case '-'                ⇒ '₋'
      case '+'                ⇒ '₊'
      case  c  if c.isDigit   ⇒ (c - '0' + '₀').toChar
      case  c                 ⇒ c
   }

   /**
    * @see  https://en.wikipedia.org/wiki/Unicode_subscripts_and_superscripts
    */
   def superscript(char : Char) : Char = char match
   {
      case '1'                ⇒ '¹'
      case '2'                ⇒ '²'
      case '3'                ⇒ '³'
      case '-'                ⇒ '⁻'
      case '+'                ⇒ '⁺'
      case  c  if c.isDigit   ⇒ (c - '0' + '⁰').toChar
      case  c                 ⇒ c
   }

   def subscript  (s : String) : String = s.map(subscript)
   def superscript(s : String) : String = s.map(superscript)
   def mod12(i : ℤ) : ℤ                 = floorMod(i,12)
}

//****************************************************************************
