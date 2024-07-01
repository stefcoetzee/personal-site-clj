title: Steel PLC
description: Software-defined PLC: RTOS written in Zig
slug: steel-plc
last-updated: 2024-06-28

# SteelPLC: RTOS implemented in Zig for use as PLC

I think there might be an argument to be made in favor of implemented a 
software-defined programmable logic controller (PLC) in Zig.

A PLC is really only a real-time operating system (RTOS).

This is something [^something].

The runtime should stand on its own.
The overarching goal is to make it amenable to deterministic simulation 
testing (DST).

In order to speed up time such as is done during DST,
the notion of time has to be external to the controller.

```clojure
(defn foo [] "foo")
```

[^something]: Hey ma! 
