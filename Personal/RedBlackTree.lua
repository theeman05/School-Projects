--[[
	My version of the RedBlackTree, RedBlackNode.
	
	Converted code from https://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
	Also added some other stuff
	
	@author: dbhs (on Roblox) / theeman05 (on github)
--]]


local RedBlackNode = {}
local RedBlackTree = {}

local NULL_VALUE_ERROR = "A value must be given"
local TREE_UNDERFLOW_ERROR = "No nodes in the tree"

local RED = true
local BLACK = false


--------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------ RedBlackNode ------------------------------------------------------------
do 
	RedBlackNode.__index = RedBlackNode
	
	-- Constructor for creating a RedBlackNode. value is expected. The others are optional
	function RedBlackNode.new(value : any, left : RedBlackNode, right : RedBlackNode, color : boolean, size : number) : RedBlackNode
		return setmetatable({
			Value = value; 		-- The value of this node
			Left = left;		-- The node's left child	
			Right = right;		-- The node's right child
			Color = color;		-- The color of this node
			Size = size or 1;	-- The size of this node
		}, RedBlackNode)
	end
end

--------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------ RedBlackTree ------------------------------------------------------------
do
	RedBlackTree.__index = RedBlackTree
	
	-- Method for comparing objects with default arguments
	function defaultComparator(value1, value2)
		if value1 < value2 then 
			return -1 -- value1 is smaller than value2
		elseif value1 > value2 then
			return 1  -- value1 is larger than value2
		else
			return 0  -- value1 is equal to value2
		end
	end
	
	-- Constructor for creating a new RedBlackTree. A comparator can be given and determines the order children are stored
	function RedBlackTree.new(comparator : (value1 : any, value2 : any) -> number)
		return setmetatable({ -- The RedBlackTree Variables
			Root 	= nil; -- Root Node
			Compare = comparator or defaultComparator
		}, RedBlackTree)
	end
	
	----------------------------------------------------------------------
	------------------------ Node Helper Methods -------------------------
	
	-- Return true if a node is red, false if it is black
	function isRed(node : RedBlackNode)
		return node ~= nil and node.Color == RED or false
	end
	
	-- The number of node in a subtree rooted at node; 0 if node is null
	function size(node : RedBlackTree) : number
		return node ~= nil and node.Size or 0
	end
	
	-- Get number of nodes in the tree (null child nodes included)
	function RedBlackTree.__len(self) : number
		return size(self.Root)
	end
	
	-- Make the tree empty
	function RedBlackTree:Clear()
		self.Root = nil
	end

	-- Check if the tree is empty
	function RedBlackTree:IsEmpty() : boolean
		return self.Root == nil
	end
	
	----------------------------------------------------------------------
	------------------- Standard Binary Search Methods -------------------
	
	-- Determine if the tree has the given value
	function RedBlackTree:Contains(value : any) : boolean
		return contains(self.Compare, self.Root, value)
	end
	
	-- Test if a value is in a subtree of a node
	function contains(comparator, node : RedBlackNode, value)
		local found = false
		local nodeValue
		while node ~= nil and found ~= true do
			local comparison = comparator(value, node.Value)
			if comparison < 0 then
				node = node.Left
			elseif comparison > 0 then
				node = node.Right
			else
				return true
			end

			found = contains(comparator, node, value)
		end

		return found
	end
	
	----------------------------------------------------------------------
	----------------------- RedBlackTree Insertion -----------------------
	
	-- Method for adding a new node into the tree
	function RedBlackTree:Insert(newValue : any)
		assert(newValue, NULL_VALUE_ERROR)
		
		self.Root = insert(self.Compare, self.Root, newValue)
		self.Root.Color = BLACK
	end
	
	-- Insert the value into the given node and return the new node
	function insert(comparator, node : RedBlackNode, newValue) : RedBlackNode
		if node == nil then
			return RedBlackNode.new(newValue, nil, nil, true)
		end
		
		local cmp = comparator(newValue, node.Value)
		
		if cmp < 0 then
			node.Left = insert(comparator, node.Left, newValue)
		elseif cmp > 0 then
			node.Right = insert(comparator, node.Right, newValue)
		else
			node.Value = newValue
		end
		
		-- Fix any right-leaning links
		if isRed(node.Right) and not isRed(node.Left) then
			node = rotateLeft(node)
		end
		if isRed(node.Left) and isRed(node.Left.Left) then
			node = rotateRight(node)
		end
		if isRed(node.Left) and isRed(node.Right) then
			flipColors(node)
		end
		
		node.Size = 1 + size(node.Left) + size(node.Right)
		return node
	end
	
	-- Insert a multiple values
	function RedBlackTree:InsertAll(...)
		assert(..., NULL_VALUE_ERROR)
		for _, value in ... do
			self:Insert(value)
		end
	end
	
	----------------------------------------------------------------------
	----------------------- RedBlackTree Deletion ------------------------
	
	-- Removes the smallest node from the table
	function RedBlackTree:RemoveMin()
		assert(not self:IsEmpty(), TREE_UNDERFLOW_ERROR)
		
		-- Check if both children of the root are black and set the root to red
		if not (isRed(self.Root.Left) and isRed(self.Root.Right))then
			self.Root.Color = RED
		end
		
		self.Root = removeMin(self.Root)
		
		if not self:IsEmpty() then
			self.Root.Color = BLACK
		end
	end
	
	function removeMin(node : RedBlackNode) : RedBlackNode
		if node.Left == nil then
			return nil
		end
		
		if not (isRed(node.Left) and isRed(node.Left.Left)) then
			node = moveRedLeft(node)
		end
		
		node.Left = removeMin(node.Left)
		return balance(node)
	end
	
	-- Removes the largest node from the table
	function RedBlackTree:RemoveMax()
		assert(not self:IsEmpty(), TREE_UNDERFLOW_ERROR)

		-- Check if both children of the root are black and set the root to red
		if not (isRed(self.Root.Left) and isRed(self.Root.Right)) then
			self.Root.Color = RED
		end

		self.Root = removeMax(self.Root)

		if not self:IsEmpty() then
			self.Root.Color = BLACK
		end
	end
	
	function removeMax(node : RedBlackNode) : RedBlackNode
		if isRed(node.Left) then
			node = rotateRight(node)
		end
		
		if node.Right == nil then
			return nil
		end
		
		if not (isRed(node.Right) and isRed(node.Right.Left)) then
			node = moveRedRight(node)
		end
		
		node.Right = removeMax(node.Right)
		return balance(node)
	end
	
	
	-- Removes the node with the given value
	function RedBlackTree:Remove(value : any)
		assert(value, NULL_VALUE_ERROR)
		
		if not self:Contains(value) then
			return
		end
		
		-- if both children of root are black, set root to red
		if not (isRed(self.Root.Left) and isRed(self.Root.Right)) then
			self.Root.Color = RED
		end
		
		self.Root = remove(self.Compare, self.Root, value)
		if not self:IsEmpty() then
			self.Root.Color = BLACK
		end
	end
	
	-- Delete the node with the given value rooted at node
	function remove(comparator, node : RedBlackNode, value)
		if comparator(value, node.Value) < 0 then
			if not (isRed(node.Left) and isRed(node.Left.Left)) then
				node = moveRedLeft(node)
			end
			node.Left = remove(comparator, node.Left, value)
		else
			if isRed(node.Left) then
				node = rotateRight(node)
			end
			if comparator(value, node.Value) == 0 and node.Right == nil then
				return nil
			end
			if not (isRed(node.Right) and isRed(node.Right.Left)) then
				node = moveRedRight(node)
			end
			if comparator(value, node.Value) == 0 then
				node.Value = min(node.Right).Value
				node.Right = removeMin(node.Right)
			else
				node.Right = remove(comparator, node.Right, value)
			end
		end
		return balance(node)
	end
	
	----------------------------------------------------------------------
	----------------------- RedBlackTree Helpers -------------------------
	
	-- make a left-leaning link lean right
	function rotateRight(node : RedBlackNode) : RedBlackNode
		assert(node ~= nil and isRed(node.Left))
		
		local x = node.Left
		node.Left = x.Right
		x.Right = node
		x.Color = node.Color
		node.Color = RED
		x.Size = node.Size
		node.Size = 1 + size(node.Left) + size(node.Right)
		return x
	end
	
	-- Make a right-leaning tree lean left
	function rotateLeft(node : RedBlackNode) : RedBlackNode
		assert(node ~= nil and isRed(node.Right))
		
		local x = node.Right
		node.Right = x.Left
		x.Left = node
		x.Color = node.Color
		node.Color = RED
		x.Size = node.Size
		node.Size = 1 + size(node.Left) + size(node.Right)
		return x
	end
	
	-- flip the colors of a node and its two children
	function flipColors(node : RedBlackNode)
		node.Color = not node.Color
		if node.Left then
			node.Left.Color = not node.Left.Color
		end
		if node.Right then
			node.Right.Color = not node.Right.Color
		end
	end
	
	-- Assuming node is red and both its left and left's left are black, make its left or one of its children red
	function moveRedLeft(node : RedBlackNode) : RedBlackNode
		flipColors(node)
		
		if node.Right and isRed(node.Right.Left) then
			node.Right = rotateRight(node.Right)
			node = rotateLeft(node)
			flipColors(node)
		end
		return node
	end 
	
	-- Assuming node is red and both its right and right's left are black, make its right or one of its children red
	function moveRedRight(node : RedBlackNode) : RedBlackNode
		flipColors(node)

		if node.Left and isRed(node.Left.Left) then
			node = rotateRight(node)
			flipColors(node)
		end
		return node
	end 
	
	-- Restrore red-black tree invariant
	function balance(node : RedBlackNode) : RedBlackNode
		if isRed(node.Right) and not isRed(node.Left) then
			node = rotateLeft(node)
		end
		if isRed(node.Left) and isRed(node.Left.Left) then
			node = rotateRight(node)
		end
		if isRed(node.Left) and isRed(node.Right) then
			flipColors(node)
		end
		
		node.Size = 1 + size(node.Left) + size(node.Right)
		return node
	end
	
	----------------------------------------------------------------------
	------------------------- Utility Function ---------------------------
	
	-- returns the height of the tree. A 1-node tree has height 0
	function RedBlackTree:Height() : number
		return height(self.Root)
	end
	
	function height(node : RedBlackNode) : number
		return node ~= nil and 1 + math.max(height(node.Left), height(node.Right)) or -1
	end
	
	----------------------------------------------------------------------
	--------------------------- Table Methods ----------------------------
	
	-- Return the smallest value in the table
	function RedBlackTree:Min()
		assert(not self:IsEmpty(), TREE_UNDERFLOW_ERROR)
		return min(self.Root).Value
	end
	
	-- The smallest node in subtree rooted at node; null if no node present
	function min(node : RedBlackNode) : RedBlackNode
		return node.left ~= nil and min(node.Left) or node
	end
	
	-- Returns the largest value in the table
	function RedBlackTree:Max()
		assert(not self:IsEmpty(), TREE_UNDERFLOW_ERROR)
		return max(self.Root).Value
	end
	
	-- The largest node in the table
	function max(node : RedBlackNode) : RedBlackNode
		return node.Right ~= nil and max(node.Right) or node
	end
	
	----------------------------------------------------------------------
	---------------------- Traversal Data Methods ------------------------
	
	-- Returns an array of values in the tree in the order: Parent, Left, Right
	function RedBlackTree:PreOrderArray() : {any}
		local t = {}
		preOrderArray(self.Root, t)
		return t
	end

	function preOrderArray(node : RedBlackNode, array)
		if node ~= nil then
			table.insert(array, node.Value)
			preOrderArray(node.Left, array)
			preOrderArray(node.Right, array)
		end
	end

	-- Returns an array of values in the tree in the order: Left, Parent, Right
	function RedBlackTree:InOrderArray() : {any}
		local t = {}
		inOrderArray(self.Root, t)
		return t
	end

	function inOrderArray(node : RedBlackNode, array)
		if node ~= nil then
			inOrderArray(node.Left, array)
			table.insert(array, node.Value)
			inOrderArray(node.Right, array)
		end
	end

	-- Returns an array of values in the tree in the order: Left, Right, Parent
	function RedBlackTree:PostOrderArray() : {any}
		local t = {}
		postOrderArray(self.Root, t)
		return t
	end

	function postOrderArray(node : RedBlackNode, array)
		if node ~= nil then
			postOrderArray(node.Left, array)
			postOrderArray(node.Right, array)
			table.insert(array, node.Value)
		end
	end
	
	----------------------------------------------------------------------
	---------------------- Traversal Print Methods ------------------------
	
	-- Print the table in the order: Parent, Left, Right
	function RedBlackTree:PreOrderPrint()
		preOrderPrint(self.Root)
	end
	
	function preOrderPrint(node : RedBlackNode)
		if node ~= nil then
			print(node.Value.. " Color: ".. (node.Color and "R" or "B"))
			preOrderPrint(node.Left)
			preOrderPrint(node.Right)
		end
	end
	
	-- Print the table in the order: Left, Parent, Right
	function RedBlackTree:InOrderPrint()
		inOrderPrint(self.Root)
	end
	
	function inOrderPrint(node : RedBlackNode)
		if node ~= nil then
			inOrderPrint(node.Left)
			print(node.Value.. " Color: ".. (node.Color and "R" or "B"))
			inOrderPrint(node.Right)
		end
	end
	
	-- Print the table in the order: Left, Right, Parent
	function RedBlackTree:PostOrderPrint()
		postOrderPrint(self.Root)
	end

	function postOrderPrint(node : RedBlackNode)
		if node ~= nil then
			postOrderPrint(node.Left)
			postOrderPrint(node.Right)
			print(node.Value.. " Color: ".. (node.Color and "R" or "B"))
		end
	end
end

return RedBlackTree
