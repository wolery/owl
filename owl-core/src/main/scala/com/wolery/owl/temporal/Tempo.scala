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
package owl
package temporal

import com.wolery.math.Piecewise

//****************************************************************************

case class Tempo(bpm: ℝ) extends AnyVal
{
  def marking: String = Tempo.rates(bpm)
}

//****************************************************************************

object Tempo
{
  private
  val rates: ℝ ⇒ String =
    Piecewise("Larghissimo",
       20.0 → "Grave",
       41.0 → "Lento",
       46.0 → "Largo",
       51.0 → "Larghetto",
       56.0 → "Adagio",
       65.0 → "Adagietto",
       70.0 → "Andante moderato",
       73.0 → "Andante",
       78.0 → "Andantino",
       84.0 → "Marcia moderato",
       86.0 → "Moderato",
       98.0 → "Allegretto",
      110.0 → "Allegro",
      133.0 → "Vivace",
      141.0 → "Vivacissimo",
      151.0 → "Allegrissimo",
      168.0 → "Presto",
      178.0 → "Prestissimo")

   private
   val names: scala.collection.Map[String,ℝ] =
     Map("larghissimo"      →  15.0,
         "grave"            →  30.5,
         "lento"            →  43.5,
         "largo"            →  48.5,
         "larghetto"        →  53.5,
         "adagio"           →  60.5,
         "adagietto"        →  67.5,
         "andante moderato" →  71.5,
         "andante"          →  75.5,
         "andantino"        →  81.0,
         "marcia moderato"  →  85.0,
         "moderato"         →  90.0,
         "allegretto"       → 104.0,
         "allegro"          → 121.5,
         "vivace"           → 137.0,
         "vivacissimo"      → 146.0,
         "allegrissimo"     → 159.5,
         "presto"           → 173.0,
         "prestissimo"      → 178.0)

  def apply(name: String): Option[Tempo] =
  {
    names.get(name.toLowerCase).map(Tempo(_))
  }
}

//****************************************************************************
