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
//*
//*
//****************************************************************************

package com.wolery.owl
package core

import util.utilities.mod

//****************************************************************************

object experimental
{
  case class ℤmod (modulus: ℤ)
  {
    require(modulus > 0)

    object Modulus{implicit object instance_ extends Modulus}
    implicit val instance = Modulus.instance_

    class  Modulus extends Torsor[X,X] with Group[X]
    {
      val e                 = X(0)
      def operate(i:X,j:X)  = X(i.i + j.i)
      def inverse(    j:X)  = X(- j.i)
      def apply(i:X,j:X): X = X(i.i + j.i)
      def delta(i:X,j:X): X = X(j.i - j.i)
    }

    def apply(i: ℤ): X = X(i)

    case class X (i: ℤ)
    {
      override def toString: String = s"$i (mod $modulus)"
    //def -(j:X):X = X(j.i-i)
    }

  }

//  case class MOD[N<: ℤmod#Modulus] (i: ℤ) extends AnyVal
//  {
//    override def toString: String = i.toString
//  }


}

//****************************************************************************
