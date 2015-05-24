/*
 * Copyright (C) 2014 adamoutler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/ .
 */

package CASUAL.misc.math;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author adamoutler
 */
public class StringMath {
   ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        
    public String performRoundedMathOperation(String mathProblem) throws CASUALMathOperationException{
        try {
            if ( mathProblem==null|| mathProblem.isEmpty())throw new CASUALMathOperationException("Math Operations cannot be blank");
            String s=engine.eval(mathProblem.replace(";",";\n")).toString();
            s = !s.contains(".") ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
            return s;
        } catch (ScriptException ex) {
            try {
                throw new CASUALMathOperationException(mathProblem+ " evaluated to: "+engine.eval(mathProblem));
            } catch (ScriptException ex1) {
                throw new CASUALMathOperationException(mathProblem+ " could not be evaluated");
            }
        }

    
    }
}
