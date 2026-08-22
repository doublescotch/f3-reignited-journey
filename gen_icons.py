# -*- coding: utf-8 -*-
"""Genere drop.png et temperature.png (10x10 RGBA, blanc sur transparent).

Glyphes 9x9 ALIGNES EN HAUT-GAUCHE de la case 10x10 : les icones de Reignited
HUD occupent 9 px de haut dans leurs cases de 10 — un glyphe pleine hauteur
depassait d'un pixel en bas de la rangee (constat en jeu, 2026-08-22). La
ligne 9 et la colonne 9 restent vides pour accueillir la passe d'ombre (+1,+1)
sans deborder de la case.
"""
import os
import struct
import zlib

DROP = [
    "....#....",
    "....#....",
    "...###...",
    "...###...",
    "..#####..",
    ".#######.",
    ".#######.",
    ".#######.",
    "..#####..",
]

TEMP = [
    "....#....",
    "...###...",
    "...#.#...",
    "...#.#...",
    "...###...",
    "..#####..",
    ".#######.",
    ".#######.",
    "..#####..",
]


def png_10x10(mask):
    lignes = [l.ljust(10, ".") for l in mask] + ["." * 10] * (10 - len(mask))
    raw = b""
    for l in lignes:
        raw += b"\x00" + b"".join(
            b"\xff\xff\xff\xff" if c == "#" else b"\x00\x00\x00\x00" for c in l)

    def chunk(t, d):
        return (struct.pack(">I", len(d)) + t + d
                + struct.pack(">I", zlib.crc32(t + d) & 0xFFFFFFFF))

    return (b"\x89PNG\r\n\x1a\n"
            + chunk(b"IHDR", struct.pack(">IIBBBBB", 10, 10, 8, 6, 0, 0, 0))
            + chunk(b"IDAT", zlib.compress(raw, 9))
            + chunk(b"IEND", b""))


base = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                    "src", "main", "resources", "assets",
                    "f3reignitedjourney", "textures", "gui")
for nom, m in (("drop", DROP), ("temperature", TEMP)):
    data = png_10x10(m)
    with open(os.path.join(base, nom + ".png"), "wb") as f:
        f.write(data)
    print(nom + ".png : " + str(len(data)) + " octets, glyphe 9x9 aligne en haut")
