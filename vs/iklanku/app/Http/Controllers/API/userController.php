<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;

class userController extends Controller
{
        /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $user = user::all();
        if($user){
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil ditampilkan',
                'data'=> $user
            ]);
        } else {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal ditampilkan'
            ]);
        }
        
    }

   
    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        try {
            $user = new user;
            $user->username = $request->username;
            $user->password = $request->password;
            $user->save();
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil ditambah',
                'data'=> $user
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal ditambah'
            ]);
        }
        
        
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $user = User::find($id);
        if($user){
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil ditambah',
                'data'=> $user
            ]);
        } else {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal ditampilkan'
            ]);
        }
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        try {
            $user = User::findOrFail($id);
            $user->password = $request->password;
            $user->save();
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil diubah',
                'data'=> $user
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal diubah'
            ]);
        }
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $user = User::find($id);
        if ($user) {
            $user->delete();
            return response()->json([
                'status'=>200,
                'message'=>'Data berhasil dihapus'
            ]);
        }else{
            return response()->json([
                'status'=>400,
                'message'=>'Data gagal dihapus'
            ]);
        }
        
    }
}
