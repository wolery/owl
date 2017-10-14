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

package com.wolery.owl
package utils

import java.util.prefs.{Preferences ⇒ JavaPreferences}

/**
 * TODO
 *
 * @tparam α  TODO
 *
 * @author Jonathon Bell
 */
trait Preference[α]
{
  def name              : String
  def value             : α
  def value_=(value: α) : Unit
  def reset()           : Unit

  final def apply()         : α      = value
  final def apply (value: α): Unit   = value_=(value)
  final def update(value: α): Unit   = value_=(value)

  override final
  def toString()        : String = s"Preference($name, $value)"
}

/**
 * TODO
 *
 * @author Jonathon Bell
 */
class Preferences(private val m_imp: JavaPreferences)
{
  def this(path: String)                    = this(JavaPreferences.userRoot.node(path))
  def this(clss: Class[_])                  = this(JavaPreferences.userNodeForPackage(clss))
  def this(prefs: Preferences,path: String) = this(prefs.m_imp.node(path))

  def node(path: String): Preferences =
  {
    new Preferences(m_imp.node(path))
  }

  private
  def atomic[α](print: α ⇒ String,
                parse: String ⇒ α)
                (key: String,default: α)
                : Preference[α] =
  {
    new Preference[α]
    {
      val dflt              : String = print(default)
      val name              : String = key
      def value             : α      = parse(m_imp.get(key,dflt))
      def value_=(value: α) : Unit   = m_imp.put(key,print(value))
      def reset()           : Unit   = m_imp.remove(name)
    }
  }

  private
  def sequence[α](parse: String ⇒ α): Sequence[α] =
  {
    atomic(_.mkString("◇"),_.split("◇").map(parse))
  }

  type Atomic[α]   = (String,α) ⇒ Preference[α]
  type Sequence[α] = (String,Seq[α]) ⇒ Preference[Seq[α]]

  val string : Atomic[String]   = atomic(_.toString,identity)
  val bool   : Atomic[Bool]     = atomic(_.toString,_.toBoolean)
  val int    : Atomic[Int]      = atomic(_.toString,_.toInt)
  val long   : Atomic[Long]     = atomic(_.toString,_.toLong)
  val float  : Atomic[Float]    = atomic(_.toString,_.toFloat)
  val double : Atomic[Double]   = atomic(_.toString,_.toDouble)

  val strings: Sequence[String] = sequence(identity)
  val bools  : Sequence[Bool]   = sequence(_.toBoolean)
  val ints   : Sequence[Int]    = sequence(_.toInt)
  val longs  : Sequence[Long]   = sequence(_.toLong)
  val floats : Sequence[Float]  = sequence(_.toFloat)
  val doubles: Sequence[Double] = sequence(_.toDouble)
}

//****************************************************************************
