program testGcd;
var x, y, result: integer;

procedure gcd(x, y: integer);
var nextY, tmp: integer;
begin
    if x < y then
    begin
        tmp := x;
        x := y;
        y := tmp;
    end;
    if y = 0 then
    begin
        result := x;
    end
    else
    begin
        nextY := x mod y;
        write('  = gcd(', y, ', ', nextY, ')');
        gcd(y, nextY);
    end;
end;

begin
    x := 36;
    y := 24;
    write('gcd(', x, ', ', y, ')');
    gcd(x, y);
    write('  = ', result);

    x := 7854;
    y := 3108;
    write('gcd(', x, ', ', y, ')');
    gcd(x, y);
    write('  = ', result);
end.
