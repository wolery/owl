//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose :
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*
//*
//****************************************************************************

package com.wolery.owl.core

//****************************************************************************

final class Scale private (val root: Note,val shape: Shape)
{
  def name: String                        = ???
  def aliases: Seq[String]                = ???
  def notes: Notes                        = ???
  def mode(mode: ℕ): Scale                = ???
  def modes: Seq[Scale]                   = ???

  override def toString: String           = s"$root $shape"
}

//****************************************************************************

object Scale
{
  def apply(n: Note*): Scale              = ???
  def apply(n: Note,s: Shape): Scale      = new Scale(n,s)
}

//****************************************************************************
