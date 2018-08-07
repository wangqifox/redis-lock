local key = KEYS[1]
local value = ARGV[1]
local oldvalue = redis.call("get", key)

if (oldvalue == value) then
    return redis.call("del", key)
else
    return 0
end
