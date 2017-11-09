//**************************** Copyright © Jonathon Bell. All rights reserved.
//*
//*
//*  Version : $Header:$
//*
//*
//*  Purpose : An idiomatic Scala interface to the core Java preferences API.
//*
//*
//*  Comments: This file uses a tab size of 2 spaces.
//*                                                                     0-0
//*                                                                   (| v |)
//**********************************************************************w*w***

package com.wolery.owl
package util

import java.util.prefs.{Preferences ⇒ JavaPreferences}

/**
 * Represents a single named, mutable, persistent, value - a ''preference'' -
 * of type `α`.
 *
 * Preferences are persisted transparently in a platform specific manner.
 *
 * Each has a sensible default value and can be reverted to this default value
 * any time.
 *
 * Supports a number of idiomatic styles of property access:
 * {{{
 *      val p: Preference[Boolean] = ...
 *
 *    if (p.value)                                        // Get preference
 *      p.value =  ...                                    // Set preference
 *    if (p())                                            // Get preference
 *      p(false)   ...                                    // Set preference
 *    else
 *      p() =      ...                                    // Set preference
 * }}}
 *
 * @tparam α  The type of value managed by the preference.
 *
 * @see    [[https://docs.oracle.com/javase/8/docs/technotes/guides/preferences/index.html
 *          Core Java Preferences API]]
 * @see    [[http://apps.tempel.org/PrefsEditor Mac OS X Prefs Editor]]
 * @author Jonathon Bell
 */
trait Preference[α]
{
  /**
   * Returns the name of the preference.
   */
  def name: String

  /**
   * Returns the current value of the preference.
   */
  def value: α

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  def value_=(value: α): Unit

  /**
   * Reverts the preference to its original default value.
   */
  def revert(): Unit

  /**
   * Returns the current value of the preference.
   */
  @inline final
  def apply(): α = value

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  @inline final
  def apply (value: α): Unit = value_=(value)

  /**
   * Updates the current value of the preference with the given value.
   *
   * @param  value  The new value for the preference.
   */
  @inline final
  def update(value: α): Unit = value_=(value)
}

/**
 * Implements a factory for creating preferences of various primitive types.
 *
 * @author Jonathon Bell
 */
class Preferences(private val m_imp: JavaPreferences)
{
  /**
   * TODO
   *
   * @param  path  TODO
   */
  def this(path: String) = 
  {
    this(JavaPreferences.userRoot.node(path))
  }

  /**
   * TODO
   *
   * @param  path  TODO
   */
  def this(klass: Class[_]) = 
  {
    this(JavaPreferences.userNodeForPackage(klass))
  }

  /**
   * TODO
   *
   * @param  preferences  TODO
   * @param  path         TODO
   */
  def this(preferences: Preferences,path: String) = 
  {
    this(preferences.m_imp.node(path))
  }

  /**
   * TODO
   *
   * @param  path  TODO
   */
  def node(path: String): Preferences =
  {
    new Preferences(m_imp.node(path))
  }

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

  /**
   *
   */
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
      def revert()           : Unit   = m_imp.remove(name)

  override final
  def toString()        : String = s"Preference($name, $value)"
    }
  }
  
  /**
   * 
   */
  private
  def sequence[α](parse: String ⇒ α): Sequence[α] =
  {
    atomic(_.mkString("◇"),_.split("◇").map(parse))
  }

  type Atomic[α]   = (String,α) ⇒ Preference[α]
  type Sequence[α] = (String,Seq[α]) ⇒ Preference[Seq[α]]
}

//****************************************************************************
