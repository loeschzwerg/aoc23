from dataclasses import dataclass
from typing import TypeAlias, Iterable, cast
import re


def prepare_data(filename: str):
    with open(filename, "r") as data:
        return data.readlines()


def match_color(draw: str, color: str) -> int:
    match = re.search(str(r"(\d+) " + color), draw)
    if isinstance(match, re.Match):
        return int(cast(re.Match, match).group(1))
    else:
        return 0


global validator


@dataclass(init=True)
class CubeSet:
    r: int
    g: int
    b: int

    @staticmethod
    def parse_from_str(subs: str):
        r = match_color(subs, r"red")
        g = match_color(subs, r"green")
        b = match_color(subs, r"blue")
        assert r >= 0 and g >= 0 and b >= 0, f"{r},{g},{b} values not in range"
        return CubeSet(r, g, b)

    def __ge__(self, c) -> bool:
        return self.r >= c.r and self.g >= c.g and self.b >= c.b

    def __repr__(self):
        return f"(R={self.r}, G={self.g}, B={self.b}, V={self.is_valid()})"

    def is_valid(self):
        return CubeSet(r=12, g=13, b=14) >= self


Draws: TypeAlias = list[CubeSet]


class Game(Iterable):
    id: int
    draws: Draws

    def __init__(self, line: str):
        _game = line.split(":")
        assert len(_game) == 2, f"#:=/=2 {_game}"
        _id_match = re.search(r"Game (\d+)", _game[0])
        _id_int = int(cast(re.Match, _id_match).group(1))
        self.id = _id_int
        _draws = _game[1].split(";")
        assert len(_draws) > 0, f"no draws in game: {_draws}"
        self.draws = list(map(CubeSet.parse_from_str, _draws))

    def __iter__(self):
        return iter(self.draws)

    def __repr__(self):
        return f"Game {self.id}: {self.draws}"


def main(filename):
    validator = CubeSet(r=12, g=13, b=14)
    assert validator == validator, "Validator is not equal to itself"
    data = prepare_data(filename)
    id_sum = list()
    for line in data:
        g = Game(line)
        print(f"G {g}")
        valid_draws = map(lambda x: validator >= x, g)
        all_valid = all(valid_draws)
        if all_valid:
            id_sum.append(g.id)
    print(sum(id_sum), id_sum)
    return sum(id_sum)


if __name__ == "__main__":
    assert main("2given.txt") == 8
    assert main("2.txt") == 2551
