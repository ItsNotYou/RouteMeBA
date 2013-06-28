@echo off
rem DOS batch file to LaTeX a Stata Journal insert
rem version 2.0.1  21dec2005

set PS=ps
set MAIN=BA

pdflatex %MAIN%
if errorlevel 1 goto latexerror
echo.

pdflatex %MAIN%
if errorlevel 1 goto latexerror
echo.

bibtex %MAIN%
if errorlevel 1 goto latexerror
echo.

bibtex %MAIN%
if errorlevel 1 goto latexerror
echo.

pdflatex %MAIN%
if errorlevel 1 goto latexerror
echo.

pdflatex %MAIN%
if errorlevel 1 goto latexerror
echo.

cp %MAIN%.pdf "C:\Users\hgessner\Dropbox\Uni\Hendriks Bachelorarbeit"
rem .\%MAIN%.pdf

:latexerror
echo error occured whith (latex %MAIN%)
echo exiting now
goto exit

:exit
